package module.dao;

import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Table(name=DaoFactory.XmlCache)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.XmlCache,
indexes={
	@Index(name="ix_01", columnNames={"processed", "processed_time", "status"}),
	} )
public class XmlCache extends BaseBo {
	
	public static enum ProcessStatus{
		Success,
		Fail
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Basic(fetch=FetchType.LAZY)
	//@Column(columnDefinition="LONGBLOB", name="xml_content") //mysql
	//@Column(name="xml_content") //oracle use this
	@Column(name="xml_content") //Using this should be enough on mysql and ms sql server	//By Wing
	@Lob
	private String xmlContent;
	
	@Column(name="processed")
	private boolean processed;
	
	@Column(name="processed_time")
	private Calendar processedTime;
	
	@Column(name="status", nullable=true)
	@Enumerated(EnumType.STRING)
	private ProcessStatus status;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getXmlContent() {
		return xmlContent;
	}

	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public Calendar getProcessedTime() {
		return processedTime;
	}

	public void setProcessedTime(Calendar processedTime) {
		this.processedTime = processedTime;
	}

	public ProcessStatus getStatus() {
		return status;
	}

	public void setStatus(ProcessStatus status) {
		this.status = status;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8021093391318974763L;

}
