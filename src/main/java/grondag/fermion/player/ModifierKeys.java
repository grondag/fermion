package grondag.fermion.player;

/**
 * Player capability to detect if ctrl or alt modifier keys are pressed.
 *
 */
public class ModifierKeys //implements ICapabilityProvider, IReadWriteNBT
{
//    @CapabilityInject(ModifierKeys.class)
//    public static Capability<ModifierKeys> CAP_INSTANCE = null;
//    
//    public static enum ModifierKey
//    {
//        CTRL_KEY,
//        ALT_KEY;
//        
//        public final int flag;
//        
//        private ModifierKey()
//        {
//           this.flag = 1 << this.ordinal(); 
//        }
//    }
//
//    /**
//     *  True if player is holding down the placement modifier key.  Not persisted.
//     */
//    private int modifierKeyFlags;
//
//    @Override
//    public boolean hasCapability(@Nonnull @Nullable Capability<?> capability, @Nullable EnumFacing facing)
//    {
//        return capability == CAP_INSTANCE;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    @Nullable
//    public <T> T getCapability(@Nonnull @Nullable Capability<T> capability, @Nullable EnumFacing facing)
//    {
//        return capability == CAP_INSTANCE ? (T) this : null;
//    }
//
//    @Override
//    public void serializeNBT(NBTTagCompound tag)
//    {
//        
//    }
//    
//    @Override
//    public void deserializeNBT(@Nullable NBTTagCompound nbt)
//    {
//        
//    }
//
//    public boolean isModifierKeyPressed(ModifierKey key)
//    {
//        return (this.modifierKeyFlags & key.flag) != 0;
//    }
//
//    public void setModifierFlags(int keyFlags)
//    {
//        this.modifierKeyFlags = keyFlags;
//    }
//
//    public static boolean isModifierKeyPressed(EntityPlayer player, ModifierKey key)
//    {
//        if(player != null)
//        {
//            ModifierKeys caps = player.getCapability(ModifierKeys.CAP_INSTANCE, null);
//            if(caps != null)
//            {
//                return caps.isModifierKeyPressed(key);
//            }
//        }
//        return false;
//    }
//    
//    public static void setModifierFlags(EntityPlayer player, int keyFlags)
//    {
//        if(player != null)
//        {
//            ModifierKeys caps = player.getCapability(ModifierKeys.CAP_INSTANCE, null);
//            if(caps != null)
//            {
//                caps.setModifierFlags(keyFlags);
//            }
//        }
//        
//    }

}
