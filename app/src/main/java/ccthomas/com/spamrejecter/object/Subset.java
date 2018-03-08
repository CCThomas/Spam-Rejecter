package ccthomas.com.spamrejecter.object;

/**
 * Created by CCThomasMac on 3/7/18.
 */

public class Subset {
    private String numbers;
    private int type;
    private boolean exception;

    public static final int FULL      = 0;
    public static final int PREFIX    = 1;
    public static final int SUBSTRING = 2;
    public static final int SUFFIX    = 3;

    public Subset(String numbers, int type, boolean exception) {
        this.numbers = numbers;
        this.type = type;
        this.exception = exception;
    }

    public boolean isException(String phoneNumber) {
        System.out.println("Exception!\nSubset w/ numbers: " + numbers + "\nand Phone Number: " + phoneNumber + "\nand Filter: " + type + "\nReturns: " + matches(phoneNumber));
        return exception && matches(phoneNumber);
    }

    public boolean blockNumber(String phoneNumber) {
        System.out.println("Block Number!\nSubset w/ numbers: " + numbers + "\nand Phone Number: " + phoneNumber + "\nand Filter: " + type + "\nReturns: " + matches(phoneNumber));
        return !exception && matches(phoneNumber);
    }

    public boolean matches(String phoneNumber) {
        // +1 (###) ###-####, w/ string format +########### has length 12
        if (phoneNumber.length() != 12) return false;

        if (type == FULL) {
            return phoneNumber.equals(numbers);
        }
        else if (type == PREFIX) {
            String prefix = phoneNumber.substring(0, numbers.length());
            return prefix.equals(numbers);
        }
        else if (type == SUBSTRING) {
            return phoneNumber.contains(numbers);
        }
        else if (type == SUFFIX) {
            String suffix = phoneNumber.substring(phoneNumber.length() - numbers.length(), phoneNumber.length());
            return suffix.equals(numbers);
        }
        else return false;
    }

    public static int convertTypeStringToInt(String typeAsString) {
        switch (typeAsString) {
            case "Full": return FULL;
            case "Prefix": return PREFIX;
            case "Substring": return SUBSTRING;
            case "Suffix": return SUFFIX;
            default: return FULL;
        }
    }

    public String getTypeAsString() {
        if (type == FULL) return "Full";
        else if (type == PREFIX) return "Prefix";
        else if (type == SUBSTRING) return "Substring";
        else if (type == SUFFIX) return "Suffix";
        else return "Full";
    }

    public String getExceptionAsString() {
        if (exception) return "Exception";
        else return "Reject";
    }

    public String getNumbers() {return numbers;}
    public int getType() {return type;}
    public boolean getException() {return exception;}

    public String getCSVFormat() {
        return numbers + "," + type + "," + exception + "\n";
    }

    public String toString() {
        return "Numbers: " + numbers + '\n' +
                "Type:   " + getTypeAsString();
    }
}
