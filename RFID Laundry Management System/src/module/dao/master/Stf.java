package module.dao.master;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import module.dao.DaoFactory;
import module.dao.master.Staff.StaffStatus;

@Entity
@Table(name=DaoFactory.STF)
public class Stf implements Serializable {

	private static final long serialVersionUID = -2621122409161173539L;



	//Log4j Logger
	private final static Logger log = Logger.getLogger( Stf.class.getSimpleName() );



	public static int stfCrdLength  = 5;



	public static enum StfOpe
	{
		INSERT(""),
		UPDATE(""),
		DELETE(""),
		FAIL(""),
		FAILA("STF cannot be null."),
//		FAILB("STFCRD cannot be null."),
		FAILC("STFNAM cannot be null."),
		FAILD("SRVDSC cannot be null."),
		FAILE("STFTYP cannot be null."),
		FAILF("OPE cannot be null."),
		FAILG("Invalid OPE value."),
		FAILH("Has the same SRVDSC value in database."),
		FAILI("Nothing found in database for SRVDSC."),
		FAILJ("STFSTS is invalid."),
		FAILK("STFTYP is invalid."),
		FAILL("Has the same STF value in database."),
		FAILM("Has the same STFCRD value in database."),
		FAILN("Nothing found in database for STF."),
		FAILO("New staff cannot be 'Leave' status."),
		FAILP("STFCRD is null but temp num generation fails. Please retry"),
		
		FAILQ("CSRVDSC already exist"),
		FAiLO("Create department Failed");
		
		private String description;
		
		StfOpe(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static enum StfSts
	{
		A(Staff.StaffStatus.Normal),
		Q(Staff.StaffStatus.Leave);
		
		private StaffStatus staffStatus;
		
		StfSts(StaffStatus staffStatus) {
			this.staffStatus = staffStatus;
		}
		
		public StaffStatus getStaffStatus() {
			return staffStatus;
		}
		
		public void setStaffStatus(StaffStatus staffStatus) {
			this.staffStatus = staffStatus;
		}
	}



	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name="STF", columnDefinition="VARCHAR(7)", nullable=false)
	private String STF;
	
	@Column(name="STFCRD", columnDefinition="VARCHAR(10)")
	private String STFCRD;
	
	@Column(name="STFCNM", columnDefinition="VARCHAR(12)")
	private String STFCNM;
	
	@Column(name="STFNAM", columnDefinition="VARCHAR(40)")
	private String STFNAM;
	
	@Column(name="CSRVDSC", columnDefinition="VARCHAR(38)")
	private String CSRVDSC;
	
	@Column(name="SRVDSC", columnDefinition="VARCHAR(50)")
	private String SRVDSC;
	
	@Column(name="STFSTS", columnDefinition="VARCHAR(1)")
	private String STFSTS;
	
	@Column(name="STFTYP", columnDefinition="VARCHAR(1)")
	private String STFTYP;
	
	@Column(name="OPE", columnDefinition="VARCHAR(6)", nullable=false)
	private String OPE;



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSTF() {
		return STF;
	}

	public void setSTF(String sTF) {
		STF = sTF;
	}

	public String getSTFCRD() {
		return STFCRD;
	}

	public void setSTFCRD(String sTFCRD) {
		STFCRD = sTFCRD;
	}

	public String getSTFCNM() {
		return STFCNM;
	}

	public void setSTFCNM(String sTFCNM) {
		STFCNM = sTFCNM;
	}

	public String getSTFNAM() {
		return STFNAM;
	}

	public void setSTFNAM(String sTFNAM) {
		STFNAM = sTFNAM;
	}

	public String getCSRVDSC() {
		return CSRVDSC;
	}

	public void setCSRVDSC(String cSRVDSC) {
		CSRVDSC = cSRVDSC;
	}

	public String getSRVDSC() {
		return SRVDSC;
	}

	public void setSRVDSC(String sRVDSC) {
		SRVDSC = sRVDSC;
	}

	public String getSTFSTS() {
		return STFSTS;
	}

	public void setSTFSTS(String sTFSTS) {
		STFSTS = sTFSTS;
	}

	public String getSTFTYP() {
		return STFTYP;
	}

	public void setSTFTYP(String sTFTYP) {
		STFTYP = sTFTYP;
	}

	public String getOPE() {
		return OPE;
	}

	public void setOPE(String oPE) {
		OPE = oPE;
	}

	//Used In SchedulerServiceImpl Only
	public void validateAllFields() throws Exception {

		StfOpe stfOpe = null;
		
		try {

			if ( STF == null ) {
				stfOpe = StfOpe.FAILA;
				throw new Exception( stfOpe.toString() );
			}
//			else if ( STFCRD == null ) {	//Check it in StaffDataSynScheduler validateSTFCRD
//				stfOpe = StfOpe.FAILB;
//				throw new Exception( stfOpe.toString() );
//			}
			else if ( STFNAM == null ) {
				stfOpe = StfOpe.FAILC;
				throw new Exception( stfOpe.toString() );
			}
			else if ( SRVDSC == null ) {
				stfOpe = StfOpe.FAILD;
				throw new Exception( stfOpe.toString() );
			}
			else if ( STFTYP == null ) {
				stfOpe = StfOpe.FAILE;
				throw new Exception( stfOpe.toString() );
			}
			else if ( OPE == null ) {
				stfOpe = StfOpe.FAILF;
				throw new Exception( stfOpe.toString() );
			}

			if ( STFCNM == null )
				STFCNM = "";
			
			if ( STFSTS == null )
				STFSTS = StfSts.A.toString();
		}
		catch (Exception e) {

			log.error( "validateAllFields: " + stfOpe.getDescription(), e );
			throw e;
		}

	}
	//Used In SchedulerServiceImpl Only
}
