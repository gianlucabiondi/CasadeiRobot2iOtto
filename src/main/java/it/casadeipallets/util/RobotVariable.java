package it.casadeipallets.util;

/**
 * Class RobotVariable correspond to a single parameter to read from robot and send to iOtto 
 * @author gianluca
 *
 */
public class RobotVariable {
    
    private static final String GOOD = "0";
    private static final String ERROR = "4";
    
    private String externalReference = null;
    private String name = null;
    private int units = -1;
    private String customUnits = null;
    private int numericType = -1;
    private String value = null;
    private String valueState = null;
    
    /**
     * Constructor: with all the informations and the value
     * @param extRef
     * @param name
     * @param units
     * @param custUnits
     * @param numType
     * @param value
     */
    public RobotVariable(String extRef, String name, String units, String custUnits, String numType, String value) {
        this.externalReference = extRef;
        this.name = name;
        this.units = Integer.parseInt(units);
        this.customUnits = custUnits;
        this.numericType = Integer.parseInt(numType);
        this.value = value;
        this.valueState = GOOD;
    }
    
    /**
     * Constructor: with all the informations
     * @param extRef
     * @param name
     * @param units
     * @param custUnits
     * @param numType
     */
    public RobotVariable(String extRef, String name, String units, String custUnits, String numType) {
        this(extRef, name, units, custUnits, numType, null);
    }
    
    public String getExternalReference() {
        return externalReference;
    }
    public String getName() {
        return name;
    }
    public int getUnits() {
        return units;
    }
    public String getCustomUnits() {
        return customUnits;
    }
    public int getNumericType() {
        return numericType;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
        this.valueState = GOOD;
    }
    public String getValueState() {
        return valueState;
    }
    public void setValueError() {
        this.valueState = ERROR;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{" + externalReference + " - " + name + " - " + units + " - " + customUnits + " - " + numericType + " - " + value + "}";
    }


}
