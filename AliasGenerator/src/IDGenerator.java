import java.util.ArrayList;
import java.util.Random;

public class IDGenerator {
    private static ArrayList<String> dbSimulator = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println(generateCorpId("Wemaco PLC Limited "));
        System.out.println(generateCorpId("Oh"));
        System.out.println(generateCorpId("Wemaco"));
        System.out.println(generateCorpId("Wemaco"));
        System.out.println(generateCorpId("Wemaco"));
        System.out.println(generateCorpId("Wemaco"));
        System.out.println(generateCorpId("Wemaco"));
        System.out.println(generateCorpId("Wemaco"));
        System.out.println(generateCorpId("Wemaco"));
        System.out.println(generateCorpId("Wemaco"));
        System.out.println(generateCorpId("Wema"));
        System.out.println(generateCorpId("Joe & Sons Limited"));
        System.out.println(generateCorpId("Oak Hospitals"));
        System.out.println(generateCorpId("Suraj Stores"));
        System.out.println(generateCorpId("Airtel Nigeria"));
        System.out.println(generateCorpId("Dangote plc"));
        System.out.println(generateCorpId("Globacom"));
        System.out.println(generateCorpId("Rajman Farms"));
        System.out.println(generateCorpId("Oriental Hotels"));
        System.out.println(generateCorpId("Lagos State University"));

        System.out.println(dbSimulator.toString());
    }

    public static String generateCorpId(String corpName) {
        String corpId = corpIdGenerator(corpName, 1);
        boolean corpIdExists = checkIfCorpIdExists(corpId);

        int trialCount = 2;
        while(corpIdExists) {
            corpId = corpIdGenerator(corpName, trialCount);
            corpIdExists = checkIfCorpIdExists(corpId);
            trialCount++;
        }

        // add corpId to dbSimulator -- same as saving to database
        dbSimulator.add(corpId);
        return corpId;
    }

    private static String corpIdGenerator(String name, int noOfTrials) {
        // remove all characters and split the name into an array of strings
        String[] names = name.replaceAll("[^a-zA-Z0-9 ]", "").split(" ");
        String corpId = "";

        if (names.length > 1) {
            // get the first two words in the name
            String firstName = names[0];
            String secondName = names[1];

            switch (noOfTrials) {
                case 1:
                    corpId = firstName;
                    break;
                case 2:
                    corpId = firstName + secondName;
                    break;
                case 3:
                    corpId = firstName +  secondName.charAt(0);
                    break;
                case 4:
                    corpId = firstName + (secondName.length() > 2 ? secondName.substring(0, 2) : secondName);
                    break;
                case 5:
                    corpId = firstName + (secondName.length() > 3 ? secondName.substring(0, 3) : secondName);
                    break;
                case 6:
                    corpId = (firstName.length() > 3 ? firstName.substring(0, 3) : firstName) + secondName.substring(0, 2);
                    break;
                case 7:
                    corpId = firstName.substring(0, 4) + secondName;
                    break;
                case 8:
                    while (corpId.length() < 6) {
                        //check if all characters in the name have been exhausted
                        if (corpId.length() == name.length()) break;

                        // assign first 2 letters of the combined name to corp id
                        String fullName = firstName + secondName;
                        corpId += fullName.substring(0, 2);

                        // get random character from the combined name & convert to string
                        int randomIndex = new Random().nextInt(name.length() - 1) + 1;
                        String str = String.valueOf(name.charAt(randomIndex));

                        // get last character of corpId & convert to string
                        String lastStr = String.valueOf(corpId.charAt(corpId.length() - 1));

                        // validate (check for duplicates and rearrange characters
                        if (corpId.contains(str)) {
                            corpId = corpId.replace(str, "");
                        }
                        if (fullName.indexOf(str) < fullName.indexOf(lastStr) )  {
                            corpId = corpId.replace(lastStr, "");
                            corpId += str;
                            corpId += lastStr;
                            continue;
                        }
                        corpId += str;
                    }
                    break;
                default:
                    corpId = null;
            }
        } else {
            switch (noOfTrials) {
                case 1:
                    corpId = name;
                    break;
                case 2:
                    // remove last character from name & use as corporate id
                    String lastStr = String.valueOf(name.charAt(name.length() - 1));
                    corpId = name.replace(lastStr, "");
                    break;
                case 3:
                    // get first few characters
                    corpId = name.substring(0, name.length() - 2);
                    break;
                case 4:
                    // get more characters
                    corpId = name.substring(0, name.length() - 3);
                    break;
                case 5:
                    // get first 3 characters
                    corpId = name.substring(0, 3);
                    break;
                case 6:
                    // use first & last character
                    corpId = name.charAt(0) + name.substring(name.length() - 1);
                    break;
                case 7:
                    // use random characters

                    // assign first letter of the name to corp id
                    corpId += name.substring(0, 1);
                    while (corpId.length() < 4) {
                        //check if all characters in the name have been exhausted
                        if (corpId.length() == name.length()) break;

                        // get random character from the combined name & convert to string
                        int randomIndex = new Random().nextInt(name.length() - 1) + 1;
                        String str = String.valueOf(name.charAt(randomIndex));

                        // get last character of corpId & convert to string
                        String last = String.valueOf(corpId.charAt(corpId.length() - 1));

                        // validate (check for duplicates and rearrange characters)
                        if (corpId.contains(str)) {
//                            System.out.println("found a duplicate");
                            continue;
                        }
                        if (name.indexOf(str) < name.indexOf(last) )  {
                            corpId = corpId.replace(last, "");
                            corpId += str;
                            corpId += last;
                            continue;
                        }
                        corpId += str;
                    }
                    break;
                default:
//                    corpId = "NoCorpId-";
                    corpId = null;
            }
        }
        // return corpId
        return corpId.toUpperCase();
    }

    public static boolean checkIfCorpIdExists(String corpId) {
        // return true or false depending on whether the corp id is in the dbSimulator list (database)
        return dbSimulator.contains(corpId);
    }
}
