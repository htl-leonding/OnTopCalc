/*	HTL Leonding	*/
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author Andreas
 */
@Entity
public class ParameterP implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String shortTerm;
    String longTerm;
    @OneToOne
    Unit unit;
    Double defaultValue;
    boolean editable;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;


    public ParameterP() {
        creationDate = new Date();
    }

    public ParameterP(String longTerm, String shortTerm, Unit unit, boolean editable) {
        this();
        this.shortTerm = shortTerm;
        this.longTerm = longTerm;
        this.unit = unit;
        this.editable = editable;
    }

    public ParameterP(String longTerm, String shortTerm, Unit unit, boolean editable,  Double defaultValue) {
        this();
        this.shortTerm = shortTerm;
        this.longTerm = longTerm;
        this.unit = unit;
        this.defaultValue = defaultValue;
        this.editable = editable;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLongTerm() {
        return longTerm;
    }

    public void setLongTerm(String longTerm) {
        this.longTerm = longTerm;
    }

    public String getShortTerm() {
        return shortTerm;
    }

    public void setShortTerm(String shortTerm) {
        this.shortTerm = shortTerm;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    public String getFormatDefaultValue() {
        //return UtilityFormat.formatValueWithShortTerm(defaultValue, unit.getShortTerm());
        return "";
    }

    public Double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String toString() {
        return "Parameter{" + "id=" + id + ", shortTerm=" + shortTerm + ", longTerm=" + longTerm + ", unit=" + unit + '}';
    }
}