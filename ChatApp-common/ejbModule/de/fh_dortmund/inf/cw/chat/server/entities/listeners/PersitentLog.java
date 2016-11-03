package de.fh_dortmund.inf.cw.chat.server.entities.listeners;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import de.fh_dortmund.inf.cw.chat.server.entities.BaseEntity;

public class PersitentLog {
	
	@PrePersist
	public void newEntity(BaseEntity o)
	{	
		o.setCreatedAt(new Date());
		o.setUpdatedAt(new Date());
	}
	
	@PreUpdate
	private void updateEntity(BaseEntity o) {
		o.setUpdatedAt(new Date());
	}
	
	
}
