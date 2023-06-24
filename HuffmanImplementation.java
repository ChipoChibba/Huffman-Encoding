import java.io.*;
import java.util.*;


/**
 * @author Chipo Chibbamulilo
 * @author Chikwanda Chisha
 * Huffman Implementation for the P-set3
 */
public class HuffmanImplementation implements Huffman{

    /**
     * Read file provided in pathName and count how many times each character appears
     *
     * @param pathName - path to a file to read
     * @return - Map with a character as a key and the number of times the character appears in the file as value
     * @throws IOException
     */

    public Map<Character, Long> countFrequencies(String pathName) throws IOException {

        //instantiating map
        Map<Character, Long> charFrequencies = new TreeMap<>();
        BufferedReader input = null;

        try {
            //opening file to be read
            input = new BufferedReader(new FileReader(pathName));


        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file because " + e.getMessage());
        }

        int c;

        while ((c= input.read()) != -1) {
            char character = (char) c;

            if (charFrequencies.containsKey(character)) {
                Long frequency = charFrequencies.get(character);
                frequency++;// increasing frequency by 1
                charFrequencies.replace(character,frequency);

            } else {
                charFrequencies.put(character, 1L);
            }
        }
            try{
                    //closing the file
                input.close();
            }
            catch (IOException e){
                System.err.println(e.getMessage());
            }
        return charFrequencies;
    }
    /**
     * Construct a code tree from a map of frequency counts. Note: this code should handle the special
     * cases of empty files or files with a single character.
     *
     * @param frequencies a map of Characters with their frequency counts from countFrequencies
     * @return the code tree.
     */
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies){
        //making comparator object
        Comparator<BinaryTree<CodeTreeElement>> compare=new Comparable<>();
        //instantiate queue
        PriorityQueue<BinaryTree<CodeTreeElement>> treePq = new PriorityQueue<>(compare);
        //looping through the map
        for (Map.Entry<Character,Long> m :frequencies.entrySet()){
            CodeTreeElement data=new CodeTreeElement(m.getValue(),m.getKey());
            BinaryTree<CodeTreeElement> b= new BinaryTree<>(data, null, null);

            //add to priority queue
            treePq.add(b);
        }
        try { // try catch if the file is empty
            int v = 7;
            if (treePq.size() == 0) {
                v /=0; // this returns an error
            }
        }
        catch (Exception e){
            System.err.println("File either is empty. CHECK YOUR FILE");
            return null;
        }

        if(treePq.size()==1){
            BinaryTree<CodeTreeElement> t2 =treePq.remove();
            CodeTreeElement resData=new CodeTreeElement(t2.data.getFrequency(), null);
            BinaryTree<CodeTreeElement> T=new BinaryTree<>(resData,null, t2);
            treePq.add(T);
        }

        while (treePq.size()!=1){

            BinaryTree<CodeTreeElement> T1=treePq.remove();
            BinaryTree<CodeTreeElement> T2=treePq.remove();
            //making frequency as data
            CodeTreeElement resData= new CodeTreeElement(T1.data.getFrequency()+T2.data.getFrequency(),null);
            //making an instance of BinaryTree class called T
            BinaryTree<CodeTreeElement> T=new BinaryTree<>(resData,T1,T2);
            //add to PriorityQueue
            treePq.add(T);
        }
        //the only element present should be the code tree
        return treePq.remove();
    }/**
     *Does the recursion required for building map of characters with appropriate codes
     *
     * @param codeTree
     * @param mapCode
     * @param code
     * @return
     */


    public static void addToMap(BinaryTree<CodeTreeElement> codeTree, Map<Character, String> mapCode, String code){
        //pre-order traversing
        if(codeTree.isLeaf()) {
            mapCode.put(codeTree.data.myChar,code);
        }
        else{
            if(codeTree.hasRight()){
                addToMap(codeTree.getRight(),mapCode,code+"1");
            }

            if(codeTree.hasLeft()){
                addToMap(codeTree.getLeft(),mapCode,code+"0");
            }
        }

    }

    /**
     * supposed to be a helper function for the compute code,
     * I used the post order traverse to do so; so that I can do this
     * @param codeTree
     * @param mapCode
     * @param code
     * @return
     */
    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree){
        //call helper function add to Map
        String code="";
        Map<Character, String> mapCode=new TreeMap<>();
        if (codeTree != null) addToMap(codeTree,mapCode,code);

        return mapCode;
    }

    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException{


        BufferedReader input = null;
        BufferedBitWriter output = null;

        try {
            //opening the files (input and output)
            input = new BufferedReader(new FileReader(pathName));
            output = new BufferedBitWriter(compressedPathName);

        } catch (FileNotFoundException e) {
            System.err.println(" Cannot open file \n" + e.getMessage());
        }

        //return error message if the input is empty
        if(input==null) {throw new IOException("This file is empty");}

        int c;

        while ((c= input.read()) != -1) {
            char character = (char) c;
            String bit=(codeMap.get(character));

            for(char i: bit.toCharArray()){
                output.writeBit(i == '1');
            }
        }

        try{
        // closing both files;
        input.close();
        output.close();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Decompress file compressedPathName and store plain text in decompressedPathName.
     * @param compressedPathName - file created by compressFile
     * @param decompressedPathName - store the decompressed text in this file, contents should match the original file before compressFile
     * @param codeTree - Tree mapping compressed data to characters
     * @throws IOException
     */
    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException{
        BufferedBitReader bitInput=null;
        BufferedWriter output= null;

        try {
            //opening files to be read
            bitInput = new BufferedBitReader(compressedPathName);
            output=new BufferedWriter(new FileWriter(decompressedPathName));

        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file \n" + e.getMessage());
        }

        //if the compressed file is empty, return error message
        if(bitInput==null) {throw new IOException("This file is compress empty");}


        //making a pointer for the tree
        BinaryTree<CodeTreeElement> curr=codeTree;

        while (bitInput.hasNext()) {
            boolean bit = bitInput.readBit();
            // do something with bit

            if(!bit){
                curr=curr.getLeft();
            }

            else{
                curr=curr.getRight();
            }

            //if we're at the leaf, write the char found onto new document
            if(curr.isLeaf()){
                output.write(curr.data.myChar);
                curr=codeTree;
            }
        }
        try{
            // closing both files;
            bitInput.close();
            output.close();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }


    public static void main(String[] args) throws IOException {

        HuffmanImplementation compress = new HuffmanImplementation();
        Map<Character, Long> map= compress.countFrequencies("pset3/USConstitution.txt");Map<Character, String> mapcode= compress.computeCodes(compress.makeCodeTree(map));
        compress.compressFile(mapcode,"pset3/USConstitution.txt","pset3/USConstitution_compressedfile.txt");
        compress.decompressFile("pset3/USConstitution_compressedfile.txt","pset3/USConstitution_decompressedfile.txt",compress.makeCodeTree(map));

        Map<Character, Long> map2= compress.countFrequencies("pset3/WarAndPeace");Map<Character, String> mapcode2=compress.computeCodes(compress.makeCodeTree(map2));
        compress.compressFile(mapcode2,"pset3/WarAndPeace","pset3/WarAndPeace_compressedfile.txt");
        compress.decompressFile("pset3/WarAndPeace_compressedfile.txt","pset3/WarAndPeace_decompressedfile.txt",compress.makeCodeTree(map2));
    }
}
