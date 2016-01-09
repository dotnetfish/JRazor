package com.superstudio.web.razor.utils;

import com.superstudio.commons.csharpbridge.RefObject;

import java.util.Iterator;



public final class EnumUtil
{
	public static <T> Iterable<T> single(T item)
	{


		return null;
	}

	public static <T> Iterable<T> prepend(T item, Iterable<T> enumerable)
	{
		// boolean isFirst=false;
		final RefObject<Boolean> isFirst=new RefObject<Boolean>(true);
		final Iterator<T> body=enumerable.iterator();
		return new Iterable<T>(){

			@Override
			public Iterator<T> iterator() {
				
				// TODO Auto-generated method stub
				return new Iterator<T>(){

					@Override
					public boolean hasNext() {
						// TODO Auto-generated method stub
						return isFirst.getRefObj() || body.hasNext();
					}

					@Override
					public T next() {
						// TODO Auto-generated method stub
						if(isFirst.getRefObj()){
							isFirst.setRefObj(false);
							return item;
						}
							
						return enumerable.iterator().next();
					}
					
				};
			}
			
		};


	}
}