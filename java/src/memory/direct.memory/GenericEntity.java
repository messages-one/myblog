package memory.direct.memory;

import java.io.Serializable;

public class GenericEntity  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1543582778125579346L;

	
	private boolean canEdit;

	//@javax.persistence.Transient
	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}


}
