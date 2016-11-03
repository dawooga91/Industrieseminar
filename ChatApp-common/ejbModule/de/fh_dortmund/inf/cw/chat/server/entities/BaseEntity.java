package de.fh_dortmund.inf.cw.chat.server.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.fh_dortmund.inf.cw.chat.server.entities.listeners.PersitentLog;

@MappedSuperclass
@EntityListeners({PersitentLog.class})
public abstract class BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private long oid;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="createdAt")
	private Date createdAt;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updatedAt")
	private Date updatedAt;

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		System.out.println("date");
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public long getOid() {
		return oid;
	}
	
	
//	@PrePersist
//	public void newEntity()
//	{	
//		System.out.println("testtttttttttttttttttttttttttttttttttttttttttt");
//		setCreatedAt(new Date());
//		setUpdatedAt(new Date());
//	}
//	
//	@PreUpdate
//	private void updateEntity() {
//		setUpdatedAt(new Date());
//	}
}
