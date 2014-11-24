package module.scheduler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import module.dao.BeansFactoryApplication;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Department;
import module.dao.master.Staff;
import module.dao.master.Stf;
import module.dao.master.Staff.StaffStatus;
import module.dao.master.Stf.StfOpe;
import module.dao.master.Stf.StfSts;
import module.dao.system.Users;
import module.service.ServiceFactory;
import module.service.all.GeneralService;
import module.service.all.MasterService;
import module.service.all.SchedulerService;
import module.service.all.SystemService;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import utils.convertor.StringConvertor;
import web.actions.BaseActionSecurity;

@Component(StaffDataSynScheduler.BEANNAME)
public class StaffDataSynScheduler {
	
	public static final String BEANNAME = "StaffDataSynScheduler";

	private static Logger log = Logger.getLogger(StaffDataSynScheduler.class);
	
	private static Object processingLockObject = new Object();		//For Skipping Policy Use
	private static Object implementingLockObject = new Object();	//For Non-Skipping Policy Use
	
	@Resource(name=ServiceFactory.SystemService)
	private SystemService systemService;
	
	@Resource(name=ServiceFactory.GeneralService)
	public GeneralService generalService;
	
	@Resource(name=ServiceFactory.MasterService)
	public MasterService masterService;
	
	@Resource(name=ServiceFactory.SchedulerService)
	public SchedulerService schedulerService;

	@Resource(name=BeansFactoryApplication.BEANNAME)
	private BeansFactoryApplication beansFactoryApplication;

	//is this job running?
	private boolean processing = false;

	public boolean isProcessing() {
		return processing;
	}



	private static StaffDataSynScheduler instance;
	
	public static StaffDataSynScheduler getInstance() {
		return instance;
	}
	
	@PostConstruct
	public void afterInit(){
		instance = this;
	}



	@Scheduled(cron="0 0 5 * * ?") //production
//	@Scheduled(fixedDelay=10000)//test only
	public void execute()
	{
		synchronized ( processingLockObject ) {		//Skipping Policy
			if( processing )
				return;
			else
				processing = true;
		}

		try {
			executeImpl();
		}
		catch (Exception e) {
			log.error( StaffDataSynScheduler.class.getSimpleName() + " has error!!!", e );
		}
		
		//end process
		processing = false;
		return;
	}
	
	public void executeImpl() throws Exception {
		
		synchronized ( implementingLockObject ) {	//Non-Skipping Policy
			
			//Normal job and service job
			try {
				List<Staff> list = staffDataSyn();
				log.info( StaffDataSynScheduler.class.getSimpleName() + " success, crud " + list.size() + " record" );
			}
			catch (Exception e) {
				log.error( StaffDataSynScheduler.class.getSimpleName() + " executeImpl: ", e);
			}
			//Normal job and service job
		}
	}
	
	public List<Staff> staffDataSyn() {

		List<Stf> stfDeleteList = new LinkedList<Stf>();
		
		List<Staff> staffCRUDList = new LinkedList<Staff>();
		
		//When there is id column name as the primary key
		List<Stf> stfList = schedulerService.findAll( Order.asc("id") );
		//When there is id column name as the primary key

		Iterator<Stf> iterator = stfList.iterator();
		while ( iterator.hasNext() ) {
			
			Stf stf = iterator.next();
			try {
				stf.validateAllFields();
			}
			catch (Exception e) {

				stf.setOPE( e.getMessage() );
				schedulerService.save(stf);
				continue;
			}
			
			String ope = stf.getOPE();
			
			//New Requirement
			//Hospital cannot send "update OPE".
			//Therefore, here is auto detecting by STF for inserting or updating.
			if ( ope.equals( Stf.StfOpe.INSERT.toString() ) ) {
				try {
					Staff staff = getStaffRecordByStf( stf.getSTF() );
					if ( staff != null ) {
						ope = Stf.StfOpe.UPDATE.toString();
					}
				}
				catch (Exception e) {
					log.info("This is auto detecting by STF for inserting or updating.");
				}
			}
			//New Requirement

			if ( ope.equals( Stf.StfOpe.INSERT.toString() ) ) {
				//1. Identify that the field "STF" is unique in database
				//2. Insert zero to the field "STFCRD" until the digit length is 5
				//3. Identify that the field "STFCRD" is unique in database
				//4. Identify that the field "STFSTS" is "A" or "Q"
				//5. Identify that the field "STFTYP" is "N" or "D"
				//6. Identify that the field "SRVDSC" can be found and exactly matched in one "Department" record in database
				
				try {

					Staff staff = new Staff();
					staff.setCode( isStfUniqueInDatabase( stf.getSTF() ) );
					staff.setCardNumber( isStfcrdUniqueInDatabase( StringConvertor.insertZero( validateSTFCRD( stf.getSTFCRD() ), Stf.stfCrdLength) ) );
					staff.setNameCht( stf.getSTFCNM() );
					staff.setNameEng( stf.getSTFNAM() );

					Department department = getDeptRecordBySrvdsc( stf.getSRVDSC() );
					
					if(department == null){
						department = addDepartment(stf.getSRVDSC(), stf.getCSRVDSC());
					}
					staff.setDept( department );



					StaffStatus stfStaffStatus = isStfstsValid( stf.getSTFSTS() ).getStaffStatus();
					if ( stfStaffStatus == StaffStatus.Leave ) {
						
						StfOpe stfOpe = StfOpe.FAILO;
						Exception exception = new Exception( stfOpe.toString() );
						log.error("staffDataSyn: When stfStaffStatus = " + stfStaffStatus);
						log.error("staffDataSyn: " + stfOpe.getDescription(), exception);
						throw exception;
					}

					staff.setStaffStatus( stfStaffStatus );
					staff.setPosition( isStftypValid( stf.getSTFTYP() ) );



					staff.setEnable(true);
					
					Users user = (Users)systemService.loadUserByUsername( BaseActionSecurity.AdminUserName );
					staff.setCreatedBy(user);
					
					masterService.save( Staff.class, staff );
					
					staffCRUDList.add( staff );
					stfDeleteList.add( stf );
				}
				catch (Exception e) {
					
					stf.setOPE( e.getMessage() );
					schedulerService.save(stf);
				}
			}
			else if ( ope.equals( Stf.StfOpe.UPDATE.toString() ) ) {
				//1. Use the field "STF" to find the unique "Staff" record in database
				//2. Insert zero to the field "STFCRD" until the digit length is 5
				//3. Identify that the new value of the field "STFCRD" is still unique in database
				//4. Identify that the field "STFSTS" is "A" or "Q"
				//5. Identify that the field "STFTYP" is "N" or "D"
				//6. Identify that the new value of the field "SRVDSC" can be found and exactly matched in one "Department" record in database
				
				
				//Allow to change the field "STFCRD" in database
				//Allow to change the field "STFCNM" in database
				//Allow to change the field "STFNAM" in database
				//Allow to change the department (SRVDSC, CSRVDSC) which the record exists in database
				//Allow to change the field "STFSTS" in database
				//Allow to change the field "STFTYP" in database
				
				try {
					Staff staff = getStaffRecordByStf( stf.getSTF() );
					
					if ( stf.getSTFCRD() == null ) {
						//Ignore
					}
					else {
						String cardNumber = StringConvertor.insertZero(stf.getSTFCRD(), Stf.stfCrdLength);
						if ( staff.getCardNumber().equals( cardNumber ) ) {
							//Ignore
						}
						else {
							staff.setCardNumber( isStfcrdUniqueInDatabase( cardNumber ) );
						}
					}
					
					staff.setNameCht( stf.getSTFCNM() );
					staff.setNameEng( stf.getSTFNAM() );



					if ( staff.getDept().getNameEng().equals( stf.getSRVDSC() ) ) {
					}
					else {
						Department department = getDeptRecordBySrvdsc( stf.getSRVDSC() );
						if(department == null){
							department = addDepartment(stf.getSRVDSC(), stf.getCSRVDSC());
						}
						
						staff.setDept( department );
					}



					boolean trigger = false;
					StaffStatus stfStaffStatus = isStfstsValid( stf.getSTFSTS() ).getStaffStatus();
					if ( staff.getStaffStatus() == StaffStatus.Normal && stfStaffStatus == StaffStatus.Leave ) {
						trigger = true;
						staff.setStaffStatus( stfStaffStatus );
					}



					staff.setPosition( isStftypValid( stf.getSTFTYP() ) );



					Users user = (Users)systemService.loadUserByUsername( BaseActionSecurity.AdminUserName );
					staff.setModifiedBy(user);
					
					if ( trigger == true )
						masterService.updateAndSaveStaff(staff);
					else
						masterService.save( Staff.class, staff );

					staffCRUDList.add( staff );
					stfDeleteList.add( stf );
				}
				catch (Exception e) {
					
					stf.setOPE( e.getMessage() );
					schedulerService.save(stf);
				}
			}
			else if ( ope.equals( Stf.StfOpe.DELETE.toString() ) ) {
				//Not In Use Now
				//1. Use the field "STF" to find the unique "Staff" record in database
				//2. Set the field "enable" to "false" of this "Staff" record
				//Not In Use Now
			}
			else if ( ope.indexOf( Stf.StfOpe.FAIL.toString() ) == 0 ) {
				//Not In Use Now
				//Ignore
				//Not In Use Now
			}
			else {
				//Return Fail Message to the field "OPE"

				StfOpe stfOpe = StfOpe.FAILG;
				Exception exception = new Exception( stfOpe.toString() );
				log.error( "When OPE = " + ope );
				log.error( "" + stfOpe.getDescription(), exception );
				stf.setOPE( exception.getMessage() );
				schedulerService.save(stf);
			}
		}

		schedulerService.deleteList(stfDeleteList);

		return staffCRUDList;
	}

	//If it is null, then generate a random five digit english chars which doesn't exist in the database
	public String validateSTFCRD(String staffCardNumber) throws Exception {
		
		String allCharacters[] = {
				"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"
				};
		Random rn = new Random();
		int minimum = 0;
		int maximum = 25;
		
		if ( staffCardNumber == null ) {

			int countLoop = 0;
			int maxLoop = 10;
			while ( true ) {
				
				countLoop = countLoop + 1;
				if ( countLoop > maxLoop ) {
					StfOpe stfOpe = StfOpe.FAILP;
					Exception exception = new Exception( stfOpe.toString() );
					log.error("validateSTFCRD: countLoop = " + countLoop + " is over maxLoop = " + maxLoop);
					throw exception;
				}

				String string = allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
						+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
						+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
						+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
						+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)];
				
				try {
					isStfcrdUniqueInDatabase( string );
					return string;
				}
				catch (Exception e) {
					//Ignore
				}
			}
		}
		else
			return staffCardNumber;
	}
	
	public Department getDeptRecordBySrvdsc(final String srvdsc) throws Exception {
		
		List<Department> deptList = masterService.findByExample(Department.class, null, null, null, 

				new CustomCriteriaHandler<Department>() {
					@Override
					public void makeCustomCriteria(Criteria criteria) {
						criteria.add( Restrictions.eq("nameEng", srvdsc) );
					}
				}
				
				, null, null);

		if ( deptList.size() == 1 )
			return deptList.get(0);
		else if ( deptList.size() > 1 ) {
			
			String deptId = "";
			Iterator<Department> iterator = deptList.iterator();
			while ( iterator.hasNext() ) {
				
				if ( deptId.isEmpty() ) {
				}
				else {
					deptId = deptId + ", ";
				}
				
				Department dept = iterator.next();
				deptId = deptId + dept.getId();
			}

			StfOpe stfOpe = StfOpe.FAILH;
			Exception exception = new Exception( stfOpe.toString() );
			log.error("getDeptRecordBySrvdsc: When SRVDSC = " + srvdsc +  " Record ID " + deptId);
			log.error("getDeptRecordBySrvdsc: " + stfOpe.getDescription(), exception);
			throw exception;
		}
		else {

			StfOpe stfOpe = StfOpe.FAILI;
			Exception exception = new Exception( stfOpe.toString() );
			log.error("getDeptRecordBySrvdsc: When SRVDSC = " + srvdsc);
			log.error("getDeptRecordBySrvdsc: " + stfOpe.getDescription(), exception);

			return null;
		}
	}
	
	public StfSts isStfstsValid(final String stfsts) throws Exception {
	
		try {
			return Stf.StfSts.valueOf( stfsts );
		}
		catch (Exception e) {

			StfOpe stfOpe = StfOpe.FAILJ;
			Exception exception = new Exception( stfOpe.toString() );
			log.error("isStfstsValid: When STFSTS = " + stfsts);
			log.error("isStfstsValid: " + stfOpe.getDescription(), exception);
			throw exception;
		}
	}
	
	public String isStftypValid(final String stftyp) throws Exception {

		Map<String, String> map = beansFactoryApplication.getgetAllStaffPositionInMap();
		if ( map.containsKey( stftyp ) )
			return map.get( stftyp );
		else {

			StfOpe stfOpe = StfOpe.FAILK;
			Exception exception = new Exception( stfOpe.toString() );
			log.error("isStftypValid: When STFTYP = "+ stftyp);
			log.error("isStftypValid: " + stfOpe.getDescription(), exception);
			throw exception;
		}
	}

	public String isStfUniqueInDatabase(final String stf) throws Exception {
		
		List<Staff> staffList = masterService.findByExample(Staff.class, null, null, null, 

				new CustomCriteriaHandler<Staff>() {
					@Override
					public void makeCustomCriteria(Criteria criteria) {
						criteria.add( Restrictions.eq("code", stf) );
					}
				}
				
				, null, null);
		
		if ( staffList.size() <= 0 ) {
			return stf;
		}
		else {
			
			String staffId = "";
			Iterator<Staff> iterator = staffList.iterator();
			while ( iterator.hasNext() ) {
				
				if ( staffId.isEmpty() ) {
				}
				else {
					staffId = staffId + ", ";
				}
				
				Staff staff = iterator.next();
				staffId = staffId + staff.getId();
			}
		
			StfOpe stfOpe = StfOpe.FAILL;
			Exception exception = new Exception( stfOpe.toString() );
			log.error("isStfUniqueInDatabase: When STF = " + stf + " Recrod ID " + staffId);
			log.error("isStfUniqueInDatabase: " + stfOpe.getDescription(), exception);
			throw exception;
		}
	}
	
	public String isStfcrdUniqueInDatabase(final String stfcrd) throws Exception {
		
		List<Staff> staffList = masterService.findByExample(Staff.class, null, null, null, 

				new CustomCriteriaHandler<Staff>() {
					@Override
					public void makeCustomCriteria(Criteria criteria) {
						criteria.add( Restrictions.eq("cardNumber", stfcrd) );
					}
				}
				
				, null, null);
		
		if ( staffList.size() <= 0 ) {
			return stfcrd;
		}
		else {
			
			String staffId = "";
			Iterator<Staff> iterator = staffList.iterator();
			while ( iterator.hasNext() ) {
				
				if ( staffId.isEmpty() ) {
				}
				else {
					staffId = staffId + ", ";
				}
				
				Staff staff = iterator.next();
				staffId = staffId + staff.getId();
			}

			StfOpe stfOpe = StfOpe.FAILM;
			Exception exception = new Exception( stfOpe.toString() );
			log.error("isStfcrdUniqueInDatabase: When STFCRD = " + stfcrd + " Record ID " + staffId);
			log.error("isStfcrdUniqueInDatabase: " + stfOpe.getDescription(), exception);
			throw exception;
		}
	}
	
	public Staff getStaffRecordByStf(final String stf) throws Exception {
		
		List<Staff> staffList = masterService.findByExample(Staff.class, null, null, null, 

				new CustomCriteriaHandler<Staff>() {
					@Override
					public void makeCustomCriteria(Criteria criteria) {
						criteria.add( Restrictions.eq("code", stf) );
					}
				},
				
				new CustomLazyHandler<Staff>()
				{
					@Override
					public void LazyList(List<Staff> list)
					{
						Iterator<Staff> it = list.iterator();
						while (it.hasNext())
						{
							Staff staff = it.next();
							staff.getDept().getNameEng();
						}
					}
				}, 
				
				null);
		
		if ( staffList.size() == 1 )
			return staffList.get(0);
		else if ( staffList.size() > 1 ) {
			
			String staffId = "";
			Iterator<Staff> iterator = staffList.iterator();
			while ( iterator.hasNext() ) {
				
				if ( staffId.isEmpty() ) {
				}
				else {
					staffId = staffId + ", ";
				}
				
				Staff staff = iterator.next();
				staffId = staffId + staff.getId();
			}

			StfOpe stfOpe = StfOpe.FAILL;
			Exception exception = new Exception( stfOpe.toString() );
			log.error("getStaffRecordByStf: When STF = " + stf + " Record ID " + staffId);
			log.error("getStaffRecordByStf: " + stfOpe.getDescription(), exception);
			throw exception;
		}
		else {

			StfOpe stfOpe = StfOpe.FAILN;
			Exception exception = new Exception( stfOpe.toString() );
			log.error("getStaffRecordByStf: When STF = " + stf);
			log.error("getStaffRecordByStf: " + stfOpe.getDescription(), exception);
			throw exception;
		}
	}
	
	private Department addDepartment(final String srvdsc, final String csrvdsc) throws Exception{
		//create department object
		Department department = new Department();
		
		department.setEnable(true);
		department.setNameEng(srvdsc);
		department.setNameCht(csrvdsc);
		
		//get user
		Users user = (Users)systemService.loadUserByUsername( BaseActionSecurity.AdminUserName );
		department.setCreatedBy(user);
		
		//save department object
		masterService.save(Department.class, department);
		
		List<Department> deptList = masterService.findByExample(Department.class, null, null, null, 

				new CustomCriteriaHandler<Department>() {
					@Override
					public void makeCustomCriteria(Criteria criteria) {
						criteria.add( Restrictions.eq("nameEng", srvdsc) );
						criteria.add( Restrictions.eq("nameCht", csrvdsc));
					}
				}
				, null, null);
		
		if(deptList.size() == 1){
			return deptList.get(0);
		}else{
			String errorMessage = "Create department Failed";
			Exception exception = new Exception( errorMessage );
			log.error("addDepartment: When SRVDSC = " + srvdsc);
			log.error("addDepartment: " + errorMessage, exception);
			throw exception;
		}
		
	}
}