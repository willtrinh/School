/*
*   Name: Will Trinh
*   ID: 17986840
*   CS 143B
*   Project 1 - File System
 */

import java.io.*;
import java.nio.file.Files;

class FileSystem {
    private static final int DCAP = 384;
    private static final int BCAP = 64;
    private PackableMemory packMem = new PackableMemory();
    // ldisk[L][B]
    // L = number of logical blocks on ldisk
    // B = block length in bytes
    // implemented as byte array
    private byte[][] ldisk = new byte[64][64];
    private byte[][] OFT = new byte[4][76];
    private byte descriptorCache[] = new byte[DCAP];
    private byte bitmapCache[] = new byte[BCAP];


    // read entire block in bytes
    private void read_block(int index, byte[] block)
    {
        System.arraycopy(ldisk[index], 0, block, 0, 64);
    }
    // write entire block in bytes
    private void write_block(int index, byte block[])
    {
        System.arraycopy(block, 0, ldisk[index], 0, 64);
    }

    // Initialize directory
    private void initialize(int index)
    {
        byte newByte[] = packMem.pack(-1);
        byte newCache[] = new byte[64];
        this.read_block(index, newCache);
        for (int i = 4; i < 63; i++) {
            for (int i2 = 0; i2 < 4; ++i2, ++i) {
                newCache[i] = newByte[i2];
            }
            i += 3;
        }
        write_block(index, newCache);
    }
    
    private void init()
    {
        byte newByte[] = packMem.pack(-1);

        for (int i = 16; i < DCAP; i++)
        {
            for (int i2 = 0; i2 < 4; i++, i2++)
                descriptorCache[i] = newByte[i2];
            i = i + 11;
        }
    }

    private int getFreeBlock()
    {
        int mask[] = new int[32];
        mask[31] = 1;

        for (int i = 30; i > -1; i--)
            mask[i] = mask[i + 1] << 1;
        int bitmap[] = new int[2];
        byte num[] = new byte[4];

        for (int i = 0, i2 = 0; i < 8; i2++, i++)
        {
            num[i2] = bitmapCache[i];
            if ((i + 1) % 4 == 0)
            {
                int val = packMem.unpack(num);
                bitmap[(i + 1) / 4 - 1] = val;
                i2 = -1;
            }
        }
        for (int i = 0; i < 2; i++)
        {
            for (int i2 = 0; i2 < 32; ++i2)
            {
                int test = bitmap[i] & mask[i2];
                if (test == 0)
                {
                    bitmap[i] = bitmap[i] | mask[i2];
                    for (int i3 = 0; i3 < 2; i3++)
                    {
                        int loc = i3 * 4;
                        byte newByte[] = packMem.pack(bitmap[i3]);
                        System.arraycopy(newByte, 0, bitmapCache, loc, 4);
                    }
                    return i * 32 + i2;
                }
            }
        }
        return -1;
    }

    private void bufferToDisk(int i)
    {
        byte posByte[] = new byte[4];
        byte indexByte[] = new byte[4];
        byte lengthByte[] = new byte[4];
        System.arraycopy(OFT[i], 64, posByte, 0, 4);
        System.arraycopy(OFT[i], 68, indexByte, 0, 4);
        System.arraycopy(OFT[i], 72, lengthByte, 0, 4);

        int pos = packMem.unpack(posByte);
        int truePos = pos / 64;
        int index = packMem.unpack(indexByte);

        byte idxByte[] = new byte[4];
        System.arraycopy(descriptorCache, index * 16 + 4 + (4 * truePos), idxByte, 0, 4);
        System.arraycopy(lengthByte, 0, descriptorCache, index * 16, 4);
        int newIndex = packMem.unpack(idxByte);

        byte dataByte[] = new byte[64];
        System.arraycopy(OFT[i], 0, dataByte, 0, 64);

        write_block(newIndex, dataByte);
    }

    private int getDir()
    {
        bufferToDisk(0);
        byte indexByte[] = new byte[4];

        for (int i = 4, i2 = 0; i < 16; ++i, ++i2) {
            indexByte[i2] = descriptorCache[i];
            if (i2 == 3) {
                int num = packMem.unpack(indexByte);
                if (num == 0) {

                    int freeBlock = getFreeBlock();
                    initialize(freeBlock);
                    read_block(freeBlock, OFT[0]);

                    byte newByte[] = packMem.pack(freeBlock);
                    System.arraycopy(newByte, 0, descriptorCache, i - 3, 4);
                    int loc = (((i - 3) - 4) / 4) * 64;
                    byte posByte[] = packMem.pack(loc);

                    System.arraycopy(posByte, 0, OFT[0], 64, 4);

                    return 0;
                } else {
                    read_block(num, OFT[0]);

                    int loc = ((i - 7) / 4) * 64;
                    // System.out.println("location c " + location);
                    byte posByte[] = packMem.pack(loc);
                    System.arraycopy(posByte, 0, OFT[0], 64, 4);

                    byte numArr[] = new byte[4];
                    for (int i3 = 4, i4 = 0; i3 < 64; ++i3, ++i4) {
                        numArr[i4] = OFT[0][i3];
                        if (i4 == 3) {
                            int location1 = packMem.unpack(numArr);
                            if (location1 == -1)
                                return i3 - 7;

                            i4 = -1;
                        }
                    }
                }
                i2 = -1;
            }

        }
        return -1;
    }

    // File System

    private int existFile(String name)
    {
        bufferToDisk(0);
        byte indexByte[] = new byte[4];

        for (int i = 4, i2 = 0; i < 16; ++i, ++i2) {
            indexByte[i2] = descriptorCache[i];
            if (i2 == 3)
            {
                int num = packMem.unpack(indexByte);
                if (num != 0) {
                    read_block(num, OFT[0]);
                    int loc = ((i - 7) / 4) * 64;
                    byte posByte[] = packMem.pack(loc);
                    System.arraycopy(posByte, 0, OFT[0], 64, 4);
                    StringBuilder getName = new StringBuilder();

                    for (int i3 = 0, i4 = 0; i3 < 64; ++i3, ++i4) {

                        if ((char) OFT[0][i3] != '\u0000')
                            getName.append((char) OFT[0][i3]);
                        if (i4 == 3)
                        {
                            if (getName.toString().equals(name))
                                return i3 - 3;

                            getName = new StringBuilder();
                            i4 = -1;
                        }
                    }
                }
                i2 = -1;
            }
        }
        return -1;
    }

    private int getDes()
    {
        byte lengthByte[] = new byte[4];

        for (int i2 = 16, i3 = 0; i2 < DCAP; ++i2, ++i3) {
            lengthByte[i3] = descriptorCache[i2];
            if (i3 == 3) {
                int length = packMem.unpack(lengthByte);

                if (length == -1) {
                    byte zeroByte[] = packMem.pack(0);

                    for (int i4 = i2 - 3, i5 = 0; i5 < 4; ++i4, ++i5) {
                        descriptorCache[i4] = zeroByte[i5];
                    }
                    return (i2 + 13) / 16 - 1;
                }
                i3 = -1;
                i2 = i2 + 12;
            }
        }

        return -1;
    }

    private void bitFlip(int pos)
    {
        int mask[] = new int[32];
        mask[31] = 1;

        for (int i = 30; i > -1; --i)
            mask[i] = mask[i + 1] << 1;

        int bitmap[] = new int[2];
        byte num[] = new byte[4];

        for (int i = 0, i2 = 0; i < 8; ++i, i2++)
        {
            num[i2] = bitmapCache[i];
            if ((i + 1) % 4 == 0) {
                // packMem.setMem(number);
                int val = packMem.unpack(num);
                bitmap[(i + 1) / 4 - 1] = val;
                i2 = -1;
            }
        }

        bitmap[pos / 32] = ~mask[pos];

        for (int i3 = 0; i3 < 2; i3++)
        {
            int loc = i3 * 4;
            byte newByte[] = packMem.pack(bitmap[i3]);

            System.arraycopy(newByte, 0, bitmapCache, loc, 4);
        }
    }

    private String create(String name)
    {
        if (existFile(name) > -1)
            return "error";
        int num = getDir();
        if (num != -1) {
            int descriptor = getDes();

            System.arraycopy(packMem.pack(0), 0, descriptorCache, 16 * descriptor, 4);

            byte desByte[] = packMem.pack(descriptor);
            for (int i = num, i2 = 0; i2 < name.length(); ++i2, i++) {
                OFT[0][i] = (byte) name.charAt(i2);
            }

            System.arraycopy(desByte, 0, OFT[0], num + 4, 4);
            return name + " created";
        }

        return "error";
    }

    private String destroy(String name)
    {
        int position = existFile(name);

        if (position > -1) {
            byte indexDes[] = new byte[4];
            System.arraycopy(OFT[0], position + 4, indexDes, 0, 4);

            int index = packMem.unpack(indexDes);

            System.arraycopy(packMem.pack(-1), 0, descriptorCache, index * 16, 4);
            byte numByte[] = new byte[4];
            for (int i = index * 16 + 4, i2 = 0; i < index * 16 + 16; ++i, ++i2)
            {
                numByte[i2] = descriptorCache[i];
                if (i2 == 3)
                {
                    int num = packMem.unpack(numByte);
                    if (num == 0)
                        break;
                    bitFlip(num);
                    System.arraycopy(packMem.pack(0), 0, descriptorCache, i - 3, 4);
                    i2 = -1;
                }
            }

            System.arraycopy(packMem.pack(0), 0, OFT[0], position, 4);
            System.arraycopy(packMem.pack(-1), 0, OFT[0], position + 4, 4);

            for (int i = 1; i < 4; ++i) {
                byte data[] = new byte[4];
                System.arraycopy(OFT[i], 68, data, 0, 4);
                int num1 = packMem.unpack(data);
                if (num1 == index) {
                    byte zerfsyte[] = packMem.pack(0);
                    System.arraycopy(zerfsyte, 0, OFT[i], 68, 4);
                    System.arraycopy(zerfsyte, 0, OFT[i], 64, 4);
                    System.arraycopy(zerfsyte, 0, OFT[i], 72, 4);
                    break;
                }
            }
            return name + " destroyed";
        }
        return "error";
    }

    private int getEmptyOFT()
    {
        byte newB[] = new byte[4];
        for (int i = 1; i < 4; ++i)
        {
            System.arraycopy(OFT[i], 68, newB, 0, 4);
            int num = packMem.unpack(newB);
            if (num == 0)
                return i;
        }
        return -1;
    }
    
    // return OFT index
    private String open(String name)
    {
        int pos = existFile(name);
        if (pos > -1)
        {
            byte indexByte[] = new byte[4];
            System.arraycopy(OFT[0], pos + 4, indexByte, 0, 4);
            int index = packMem.unpack(indexByte);
            for (int i = 1; i < 4; ++i)
            {
                byte index2[] = new byte[4];
                System.arraycopy(OFT[i], 68, index2, 0, 4);
                int num = packMem.unpack(index2);
                if (index == num)
                    return "error";
            }

            byte checkByte[] = new byte[4];
            System.arraycopy(descriptorCache, index * 16 + 4, checkByte, 0, 4);
            int check = packMem.unpack(checkByte);

            if (check == 0)
            {
                byte block[] = packMem.pack(getFreeBlock());
                System.arraycopy(block, 0, descriptorCache, 16 * index + 4, 4);
            }
            int emptyIndex = getEmptyOFT();
            if (emptyIndex > -1)
            {
                // getting the first block to fill
                byte firstBlock[] = new byte[4];
                System.arraycopy(descriptorCache, index * 16 + 4, firstBlock, 0, 4);
                int first = packMem.unpack(firstBlock);
                read_block(first, OFT[emptyIndex]);

                System.arraycopy(packMem.pack(0), 0, OFT[emptyIndex], 64, 4);
                System.arraycopy(indexByte, 0, OFT[emptyIndex], 68, 4);
                System.arraycopy(descriptorCache, index * 16, OFT[emptyIndex], 72, 4);
                return (name + " opened " + emptyIndex);
            }
        }
        return "error";
    }

    private String close(int index)
    {
        byte desIn[] = new byte[4];
        System.arraycopy(OFT[index], 68, desIn, 0, 4);
        int num = packMem.unpack(desIn);
        if (num > 0 && index > 0) {
            bufferToDisk(index);
            byte zerfsyte[] = packMem.pack(0);

            System.arraycopy(zerfsyte, 0, OFT[index], 64, 4);
            System.arraycopy(zerfsyte, 0, OFT[index], 68, 4);
            System.arraycopy(zerfsyte, 0, OFT[index], 72, 4);
            return index + " closed";
        }
        return "error";

    }

    private String write(int index, char kar, int num)
    {
        byte checkByte[] = new byte[4];
        System.arraycopy(OFT[index], 68, checkByte, 0, 4);
        int check = packMem.unpack(checkByte);

        if (check > 0 && index > 0) {
            byte positionByte[] = new byte[4];
            System.arraycopy(OFT[index], 64, positionByte, 0, 4);
            int pos = packMem.unpack(positionByte);
            int truePos;

            byte indexByte[] = new byte[4];
            System.arraycopy(OFT[index], 68, indexByte, 0, 4);
            int indexDes = packMem.unpack(indexByte);

            byte lengthByte[] = new byte[4];
            System.arraycopy(OFT[index], 72, lengthByte, 0, 4);
            int length = packMem.unpack(lengthByte);

            if (length > 0)
                pos++;
            int blockSize = (pos / 64 + 1) * 64;
            int count = 0;

            for (int i = 0; i < num && pos < 192; i++, pos++) {
                if (pos > blockSize - 1) {
                    bufferToDisk(index);
                    int block = getFreeBlock();
                    byte blockByte[] = packMem.pack(block);
                    int destPos = (16 * indexDes + 4) + (pos / 64 * 4);
                    System.arraycopy(blockByte, 0, descriptorCache, destPos, 4);
                    read_block(block, OFT[index]);
                    blockSize = (pos / 64 + 1) * 64;
                }
                truePos = pos % 64;

                OFT[index][truePos] = (byte) kar;
                ++length;
                ++count;
            }

            System.arraycopy(packMem.pack(pos - 1), 0, OFT[index], 64, 4);
            System.arraycopy(packMem.pack(length), 0, OFT[index], 72, 4);
            return count + " bytes written";
        }
        return "error";
    }

    private String read(int index, int num) {

        byte checkByte[] = new byte[4];
        System.arraycopy(OFT[index], 68, checkByte, 0, 4);
        int check = packMem.unpack(checkByte);
        // check if file exist
        char output[] = new char[num];
        int count = 0;
        if (check > 0 && index > 0) {
            byte indexByte[] = new byte[4];
            System.arraycopy(OFT[index], 68, indexByte, 0, 4);
            int indexDes = packMem.unpack(indexByte);

            byte lengthByte[] = new byte[4];
            System.arraycopy(OFT[index], 72, lengthByte, 0, 4);
            int length = packMem.unpack(lengthByte);

            byte positionByte[] = new byte[4];
            System.arraycopy(OFT[index], 64, positionByte, 0, 4);
            int pos = packMem.unpack(positionByte);
            int blockSize = (pos / 64 + 1) * 64;
            int truePos;

            for (int i = 0; i < num && pos < length; ++i, ++pos) {
                if (pos > blockSize - 1) {
                    bufferToDisk(index);
                    byte blockByte[] = new byte[4];
                    int startPos = (16 * indexDes + 4) + (pos / 64 * 4);
                    System.arraycopy(descriptorCache, startPos, blockByte, 0, 4);
                    int block = packMem.unpack(blockByte);
                    read_block(block, OFT[index]);
                    blockSize = (pos / 64 + 1) * 64;
                }
                truePos = pos % 64;

                output[i] = (char) OFT[index][truePos];
                ++count;
            }
            System.arraycopy(packMem.pack(pos - 1), 0, OFT[index], 64, 4);
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < count; ++i) {
                str.append(output[i]);
            }
            return str.toString();
        }
        return "error";
    }

    private String seek(int index, int pos)
    {
        byte checkByte[] = new byte[4];
        System.arraycopy(OFT[index], 68, checkByte, 0, 4);
        int check = packMem.unpack(checkByte);
        // check if file exist
        if (check > 0 && index > 0)
        {
            byte positionByte[] = new byte[4];
            System.arraycopy(OFT[index], 64, positionByte, 0, 4);
            int position = packMem.unpack(positionByte);
            int blockSize = (position / 64 + 1) * 64;

            byte indexByte[] = new byte[4];
            System.arraycopy(OFT[index], 68, indexByte, 0, 4);
            int indexDes = packMem.unpack(indexByte);

            byte lengthByte[] = new byte[4];
            System.arraycopy(OFT[index], 72, lengthByte, 0, 4);
            int length = packMem.unpack(lengthByte);

            if (pos < length)
            {
                if (pos > blockSize - 1 || pos < blockSize - 64)
                {
                    bufferToDisk(index);
                    byte blockByte[] = new byte[4];
                    int startPos = (16 * indexDes + 4) + (pos / 64 * 4);
                    System.arraycopy(descriptorCache, startPos, blockByte, 0, 4);
                    int block = packMem.unpack(blockByte);
                    read_block(block, OFT[index]);
                }

                System.arraycopy(packMem.pack(pos), 0, OFT[index], 64, 4);
                return "posistion is " + pos;
            }
        }
        return "error";
    }

    private String directory()
    {
        bufferToDisk(0);
        byte blockByte[] = new byte[4];
        System.arraycopy(descriptorCache, 4, blockByte, 0, 4);
        int pos = 0;
        int block = packMem.unpack(blockByte);
        byte num[] = new byte[4];
        StringBuilder nameByte = new StringBuilder();
        read_block(7, OFT[0]);
        StringBuilder name = new StringBuilder();
        while (block > 0)
        {
            for (int i = 4, i2 = 0; i < 64; ++i, ++i2)
            {
                num[i2] = OFT[0][i];
                nameByte.append((char) OFT[0][i - 4]);

                if (i2 == 3)
                {
                    int num1 = packMem.unpack(num);
                    if (num1 > -1)
                        name.append(nameByte);

                    i += 4;
                    i2 = -1;
                    nameByte = new StringBuilder();
                }
            }
            int startPos = 8 + (pos / 64 * 4);
            System.arraycopy(descriptorCache, startPos, blockByte, 0, 4);
            block = packMem.unpack(blockByte);
            if (block > 0)
                pos += 64;
        }
        System.arraycopy(packMem.pack(pos), 0, OFT[0], 64, 0);
        if (name.length() > 0)
            return name.toString();
        else return "error";
    }

    private void clearCache()
    {
        byte newBlock[] = new byte[64];
        for (int i = 0; i < 64; ++i)
            write_block(i, newBlock);

        OFT = new byte[4][76];
        descriptorCache = new byte[DCAP];
        bitmapCache = new byte[BCAP];
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length > 1)
        {
            System.out.println("Invalid number of argument!");
            System.exit(1);
        }
        File file = new File ("D:/17986840.txt");

        FileOutputStream fostream = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fostream);
        System.setOut(ps);
        FileSystem fs = new FileSystem();
        FileInputStream fstream = null;
        try
        {
            fstream = new FileInputStream(args[0]);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        assert fstream != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String inLine;
        int count = 0;
        while ((inLine = br.readLine()) != null)
        {
            String[] splited = inLine.split(" ");

            switch (splited[0])
            {
                // initialize
                case "in":

                    if (splited.length == 1)
                    {
                        fs.clearCache();
                        fs.init();

                        for (int i = 0; i < 8; ++i) {
                            int test = fs.getFreeBlock();
                        }
                        byte newByte[] = fs.packMem.pack(7);
                        System.arraycopy(newByte, 0, fs.descriptorCache, 4, 4);

                        fs.initialize(7);

                        byte content[] = new byte[64];
                        fs.read_block(7, content);
                        System.arraycopy(content, 0, fs.OFT[0], 0, 64);
                        System.setOut(ps);
                        if (count == 0)
                        	{
                        		System.out.println("disk initialized");
                        		count++;
                        	}
                    	else 
                    		System.out.println("\ndisk initialized");
                    }
                    else
                        {
                        fs.clearCache();
                        System.setOut(ps);
                        System.out.println("disk restored");
                        byte[] array = Files.readAllBytes(new File(splited[1]).toPath());

                        for (int i = 0, i2 = 0; i < 64; ++i, i2 += 64)
                        {
                            byte temp[] = new byte[64];
                            System.arraycopy(array, i2, temp, 0, 64);
                            fs.write_block(i, temp);
                        }
                        byte content[] = new byte[64];
                        fs.read_block(0, content);
                        System.arraycopy(content, 0, fs.bitmapCache, 0, 64);

                        for (int i = 1, i2 = 0; i < 7; ++i, i2 += 64)
                        {
                            byte data[] = new byte[64];
                            fs.read_block(i, data);
                            System.arraycopy(data, 0, fs.descriptorCache, i2, 64);
                        }

                        fs.read_block(7, content);
                        System.arraycopy(content, 0, fs.OFT[0], 0, 64);
                    }
                    break;
                    // save
                case "sv":
                    for (int i = 0; i < 4; ++i)
                        fs.close(i);

                    fs.write_block(0, fs.bitmapCache);

                    for (int i = 0, i2 = 1; i2 < 7 && i < DCAP; i += 64, ++i2)
                    {
                        byte temp[] = new byte[64];
                        System.arraycopy(fs.descriptorCache, i, temp, 0, 64);
                        fs.write_block(i2, temp);
                    }

                    try (FileOutputStream stream = new FileOutputStream(splited[1]))
                    {
                        for (int i = 0; i < 64; ++i)
                            stream.write(fs.ldisk[i]);
                    }
                    System.setOut(ps);
                    System.out.println("disk saved");

                    break;
                    // create
                case "cr":
                    System.setOut(ps);
                    System.out.println(fs.create(splited[1]));
                    break;
                    // destroy
                case "de":
                    System.setOut(ps);
                    System.out.println(fs.destroy(splited[1]));
                    break;
                    // open
                case "op":
                    System.setOut(ps);
                    System.out.println(fs.open(splited[1]));
                    break;
                    // close
                case "cl":
                    System.setOut(ps);
                    System.out.println(fs.close(Integer.parseInt(splited[1])));
                    break;
                    // read
                case "rd":
                    System.setOut(ps);
                    System.out.println(fs.read(Integer.parseInt(splited[1]), Integer.parseInt(splited[2])));
                    break;
                    // write
                case "wr":
                    System.setOut(ps);
                    System.out.println(fs.write(Integer.parseInt(splited[1]), splited[2].charAt(0), Integer.parseInt(splited[3])));
                    break;
                    // seek
                case "sk":
                    System.setOut(ps);
                    System.out.println(fs.seek(Integer.parseInt(splited[1]), Integer.parseInt(splited[2])));
                    break;
                    // directory
                case "dr":
                    System.setOut(ps);
                    System.out.println(fs.directory());
                    break;


            }
        }
        System.exit(0);
    }
}
