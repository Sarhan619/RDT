

public class ByteArrayUtils {
    public static final int   No_of_bits_in_byte = 8;
    public static final short Mask_to_byte       = 0xFF;
    public static final int   Int_size_in_bytes  = 4;

    public static void writeInt( byte[] p_desination, int p_toWrite )
    {
		assert( p_desination.length >= Int_size_in_bytes ): 
			"Error: p_destination is too short to hold an int";
	
	
		p_desination[0] = (byte)(p_toWrite & Mask_to_byte);
		p_toWrite >>= No_of_bits_in_byte;
    
		p_desination[1] = (byte)(p_toWrite & Mask_to_byte);
		p_toWrite >>= No_of_bits_in_byte;

		p_desination[2] = (byte)(p_toWrite & Mask_to_byte);
		p_toWrite >>= No_of_bits_in_byte;

		p_desination[3] = (byte)(p_toWrite & Mask_to_byte);
    }

    public static int readInt( byte[] p_source ) 
    {
		assert( p_source.length >= Int_size_in_bytes ): 
			"Error: p_source is too short to hold an int";

	
		int result = (   p_source[ 0 ] & Mask_to_byte );
		result    |= ( ( p_source[ 1 ] & Mask_to_byte ) << 8 );
		result    |= ( ( p_source[ 2 ] & Mask_to_byte ) << 16 );
		result    |= ( ( p_source[ 3 ] & Mask_to_byte ) << 24 );

		return result;
    }
}
