package ssrv.streamer;

import java.util.concurrent.atomic.AtomicReference;

import ssrv.flv.FLVTag;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FLVStreamerClient
{
	private AtomicReference<FLVPackHolder> updateHolder;
	private FLVStreamingClientScheduler scheduler;
	private FLVHeaders headers;
	
	private FLVPackHolder prevHolder;
	private Iterator<FLVTag> current;
	
	/**
	 * constructor.
	 * @param _updateHolder AtomicReference to FLVPackHolder
	 * @param _header Headers
	 * @param _scheduler Scheduler
	 */
	public FLVStreamerClient(AtomicReference<FLVPackHolder> _updateHolder,FLVHeaders _headers,FLVStreamingClientScheduler _scheduler)
	{
		updateHolder = _updateHolder;
		scheduler = _scheduler;
		headers = _headers;
		prevHolder = null;
	}
	
	public FLVHeaders getHeaders()
	{
		return headers;
	}
	
	/**
	 * get next FLVTag
	 * @return FLVTag
	 * @throws InterruptedException blocks when need to refresh FLVPackHolder
	 */
	public FLVTag getNextTag() throws InterruptedException
	{
		FLVTag next = null;
		if(current == null){
			//lazy initialize.
			FLVPackHolder holder = updateHolder.get();
			
			if(holder == FLVPackHolder.CONSTRUCTING){
				//FLVPackHolder is not ready yet! sleep and try again.
				scheduler.sleep();
				next = getNextTag();
			}else if(holder == FLVPackHolder.FINISH){
				//FLVStream is finished already.
				next = null;
			}else{
				//otherwise , FLVPackHolder is ready.
				//this is first time to call this method , so we have to send headers too.
				//and we will send twice length FLV of interval time.
				FLVPack first = holder.getPreviousPack();
				FLVPack second = holder.getCurrentPack();
				FLVPackCollection collection = new FLVPackCollection(Arrays.asList(first,second));
				current = collection.iterator();
				prevHolder = holder;
				
				//call self.
				next = getNextTag();
			}
		}else if(current.hasNext()){
			next = current.next();
		}else{
			//here is when current.hasNext() returned false.
			//we need refresh FLVPackHolder to get new FLVTags
			FLVPackHolder update = updateHolder.get();
			if(prevHolder == update){
				//there is no update.
				scheduler.sleep();
			}else if(update == FLVPackHolder.FINISH){
				//no update anymore.
				return null;
			}else{
				//update detected.
				FLVPack pack = update.getCurrentPack();
				current = pack.iterator();
				prevHolder = update;
			}
			next = getNextTag();
		}
		
		return next;
	}
	
	private static class FLVPackCollection implements FLVPack
	{
		//CopyOnWriteArrayList , better use linked list.
		private CopyOnWriteArrayList<FLVPack> queue;
		
		@SuppressWarnings("unused")
		public FLVPackCollection()
		{
			queue = new CopyOnWriteArrayList<FLVPack>();
		}
		
		public FLVPackCollection(List<FLVPack> _list)
		{
			queue = new CopyOnWriteArrayList<FLVPack>(_list);
		}
		
		@SuppressWarnings("unused")
		public void add(FLVPack _pack)
		{
			queue.add(_pack);
		}

		@Override
		public Iterator<FLVTag> iterator()
		{
			return new FLVTagIteratorImpl(queue);
		}
		
		private static class FLVTagIteratorImpl implements Iterator<FLVTag>
		{
			private Iterator<FLVTag> tagIterator;
			private Iterator<FLVPack> packIterator;
			
			public FLVTagIteratorImpl(List<FLVPack> _packList)
			{
				packIterator = _packList.iterator();
				if(!packIterator.hasNext()){
					tagIterator = FLVPack.NULL_PACK.iterator();
				}else{
					tagIterator = packIterator.next().iterator();
				}
			}

			@Override
			public boolean hasNext()
			{
				if(tagIterator.hasNext()){
					return true;
				}else{
					if(!packIterator.hasNext()){
						return false;
					}
					tagIterator = packIterator.next().iterator();
				}
				
				return hasNext();
			}

			@Override
			public FLVTag next()
			{
				return tagIterator.next();
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		}
	}
}
