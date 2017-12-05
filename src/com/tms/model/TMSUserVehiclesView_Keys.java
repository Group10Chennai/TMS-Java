package com.tms.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TMSUserVehiclesView_Keys implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name="VehId")
	private long vehId;
	
	@Column(name = "UserId")
	private long userId;

	public long getVehId() {
		return vehId;
	}

	public long getUserId() {
		return userId;
	}
		
	public TMSUserVehiclesView_Keys() {
	}

	public TMSUserVehiclesView_Keys(long vehId, long userId) {
		this.vehId = vehId;
		this.userId = userId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TMSUserVehiclesView_Keys)) return false;
        TMSUserVehiclesView_Keys that = (TMSUserVehiclesView_Keys) o;
        return Objects.equals(getVehId(), that.getVehId()) &&
                Objects.equals(getUserId(), that.getUserId());
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(getVehId(), getUserId());
    }
}
