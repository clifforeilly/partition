
import java.io.*;
import java.util.List;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by cliff on 31/05/2014.
 */

//args
// 0=input folder
// 1=output folder
// 2=type
// 3=binary for delete content of output folder before starting parse
// 4=ostype




public class partition
{
    static String InputFileFolder = "";
    static String OutputFileFolder = "";
    static int type = 0;
    static int numsections = 10;
    static String[][] its = new String[3][numsections];

    public static void main(String[] args)
    {
        try
        {
            if(args.length>0)
            {
                System.out.println(args[0]);
                InputFileFolder=args[0];
                System.out.println(args[1]);
                OutputFileFolder=args[1];
                System.out.println(args[2]);
                type=Integer.parseInt(args[2]);

                if(Integer.parseInt(args[3])==1)      //delete the output before parsing
                {
                    File f = new File(OutputFileFolder);
                    File[] matchingFiles = f.listFiles();

                    if(matchingFiles!=null)
                    {
                        for(File tf : matchingFiles)
                        {
                            tf.delete();
                        }
                    }
                }

                String slash;
                String osType = args[4];
                if(osType.equals("Mac"))
                {
                    slash="/";
                }
                else
                {
                    slash="\\";
                }


                File f = new File(InputFileFolder);
                File[] matchingFiles = f.listFiles();
                System.out.println("Scanning ... " + InputFileFolder);
                List inputdata;

                for(File tf : matchingFiles)
                {
                    //results[0]=tf.getName();
                    System.out.println("Processing file ... " + tf.getName());
                    String filename = InputFileFolder + slash + tf.getName();
                    CSVReader csvdata = new CSVReader(new FileReader(filename));
                    inputdata = csvdata.readAll();
                    csvdata.close();

                    CSVWriter csvtest=null;
                    CSVWriter csvtrain=null;

                    int rownumheldout = inputdata.size()/10;
                    System.out.println(rownumheldout);

                    int startpos = 0;
                    int endpos = rownumheldout;
                    for(int ns=0 ; ns<numsections ; ns++)
                    {
                        int thisit=ns+1;
                        String trainfilename = OutputFileFolder + slash + "it" + thisit + "-train" + thisit + "-" + tf.getName();
                        String testfilename = OutputFileFolder + slash + "it" + thisit + "-test" + thisit + "-" + tf.getName();
                        csvtest = new CSVWriter(new FileWriter(testfilename));
                        csvtrain = new CSVWriter(new FileWriter(trainfilename));

                        int count = 0;

                        for(Object r : inputdata)
                        {
                            String[] row;
                            row = (String[]) r;
                        /*
                        row[0]=String.valueOf(count+1);
                        String tempLabels=row[LabelsColumn-2] + " " + row[LabelsColumn];
                        //row[LabelsColumn-1]=row[LabelsColumn-1] + " " + row[LabelsColumn];

                        String[] trow = new String[row.length-2];

                        for(int h=0 ; h<trow.length-2 ; h++)
                        {
                            trow[h]=row[h];
                        }
                        trow[LabelsColumn-2]=tempLabels;

                        */
                            String[] trow = new String[row.length];
                            trow[0]=String.valueOf(count+1);
                            trow[1]=row[0];
                            trow[2]=row[3].trim();
                            trow[3]=row[4].trim();

                            if(count>=startpos & count<endpos)
                            {
                                csvtest.writeNext(row);
                            }
                            else
                            {
                                csvtrain.writeNext(row);
                            }

                            count++;

                        }

                        csvtest.close();
                        csvtrain.close();
                        startpos=startpos+rownumheldout;
                        endpos=endpos+rownumheldout;

                        //runLDA(thisit, trainfilename, testfilename);
                        its[0][ns]=OutputFileFolder + slash + "it" + thisit + "-train" + thisit + "-label-topic-distributions.csv";
                        its[1][ns]=OutputFileFolder + slash + "it" + thisit + "-test" + thisit + "-document-topic-distributions.csv";
                        its[2][ns]=testfilename;
                    }

                }

                CSVWriter csvits = new CSVWriter(new FileWriter(OutputFileFolder + slash + "its.csv"));
                for(int y=0 ; y<numsections ; y++)
                {
                    String[] tmp = new String[3];
                    tmp[0]=its[0][y].replace("\\", "\\\\");
                    tmp[1]=its[1][y].replace("\\", "\\\\");
                    tmp[2]=its[2][y].replace("\\", "\\\\");
                    csvits.writeNext(tmp);
                }
                csvits.close();
            }
            else
            {
                System.out.println("Error - no parameters supplied");
            }
        }
        catch (Exception ex)
        {
            System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }
}
