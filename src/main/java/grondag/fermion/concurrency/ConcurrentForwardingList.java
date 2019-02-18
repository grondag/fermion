package grondag.fermion.concurrency;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ForwardingList;

/**
 * A forwarding list containing a volatile delegate.  
 * The delegate should never be modified directly but should instead be replaced whenever the list changes.
 */
public class ConcurrentForwardingList<T> extends ForwardingList<T> 
{
    private volatile List<T> delegate;
    
    public ConcurrentForwardingList(List<T> delegate)
    {
        this.delegate = delegate;
    }
    
    public void setDelegate(List<T> delegate)
    {
        this.delegate = delegate;
    }
    
    @Override
    protected @Nonnull List<T> delegate()
    {
        return this.delegate;
    }

}
