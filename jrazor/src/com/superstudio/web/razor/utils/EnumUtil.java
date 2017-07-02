package com.superstudio.web.razor.utils;

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
		//final RefObject<Boolean> isFirst=new RefObject<Boolean>(true);
		final Iterator<T> body=enumerable.iterator();
		return new Iterable<T>(){

			@Override
			public Iterator<T> iterator() {
					return new Iterator<T>(){
						private boolean isFirst=true;

					@Override
					public boolean hasNext() {

						return isFirst || body.hasNext();
					}

					@Override
					public T next() {

						if(isFirst){
							isFirst=false;
							return item;
						}
							
						return enumerable.iterator().next();
					}
					
				};
			}
			
		};


	}
}