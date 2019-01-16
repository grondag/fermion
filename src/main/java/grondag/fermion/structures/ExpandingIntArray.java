package grondag.fermion.structures;

public class ExpandingIntArray {
    int length = 64;
    int[] data = new int[length];
    int size = 0;
    
    public void clear() {
        size = 0;
    }
    
    private void ensureAdditionalCapacity(int toBeAdded) {
        if(size + toBeAdded > length) {
            do  
                length *= 2;
            while(length < size + toBeAdded);
            
            int[] newData = new int[length];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
        }
    }
    
    public void append(int value) {
        ensureAdditionalCapacity(1);
        data[size++] = value;
    }
    
    /**
     * Returns starting index, ensures capacity and increases size.
     */
    public int prepareForCopyIn(int sizeToAdd) {
        ensureAdditionalCapacity(sizeToAdd);
        int result = size;
        size += sizeToAdd;
        return result;
    }
    
    public int[] array() {
        return data;
    }

    public int size() {
        return size;
    }
}
