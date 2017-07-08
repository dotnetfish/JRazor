package com.superstudio.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionHelper {
	public static <T> T firstOrDefault(Iterable<T> source) {

		if (source == null || !source.iterator().hasNext())
			return null;
		return source.iterator().next();

	}

	public static <T> T firstOrDefault(Iterable<T> source, Predicate<T> action) {
		if (source == null || !source.iterator().hasNext())
			return null;
		for (T item : source) {
			if (action.test(item))
				return item;
		}
		return null;

	}

	public static <T> T lastOrDefault(List<T> source) {
		if (source == null || source.size() == 0)
			return null;

		return source.get(source.size() - 1);

	}

	public static <T> T lastOrDefault(Iterable<T> source) {
		if (source == null || !source.iterator().hasNext())
			return null;
		T result = null;
		Iterator<T> it = source.iterator();
		while (it.hasNext()) {
			result = it.next();
			// source
		}
		return result;

	}

	public static <T, Tout> List<Tout> select(Iterable<T> source, Function<T, Tout> action) {
		if (source == null || !source.iterator().hasNext())
			return Collections.emptyList();
		List<Tout> result = new ArrayList<Tout>();
		for (T item : source) {
			result.add(action.apply(item));
		}

		return result;

	}

	public static <T, Tout> boolean any(List<T> source, Predicate<T> action) {
		if (source == null || source.size() == 0)
			return false;

		for (T item : source) {
			if (action.test(item)) {
				return true;
			}
		}

		return false;

	}

	public static <T, Tout> boolean all(Iterable<T> source, Predicate<T> action) {
		if (source == null)
			return false;
		boolean result = false;
		for (T item : source) {
			if (!result) {
				result = true;
			}

			if (!action.test(item)) {
				return false;
			}
		}

		return result;

	}

	public static boolean all(char[] source, Predicate<Character> action) {
		if (source == null || source.length == 0)
			return false;

		int count = source.length;
		for (int i = 0; i < count; ++i) {

			if (!action.test(source[i])) {
				return false;
			}
		}

		return true;

	}

	/*public static <T> int sum(Iterable<T> source, Function<T, Integer> func) {

		if (source == null || !source.iterator().hasNext())
			return 0;
		Iterator<T> it = source.iterator();
		int count = 0;
		for(T item:source){
			count += func.apply(item);
		}
		*//*for (T item = it.next(); it.hasNext();it.) {
			count += func.apply(item);
		}*//*
		return count;

	}
*/
	public static <TSource, TAccumulate, TResult> TResult aggregate(List<TSource> source, TAccumulate seed,
																	BiFunction<TAccumulate, TSource, TAccumulate> func, Function<TAccumulate, TResult> resultSelector) {



		// public static String aggregate(List<ISymbol> symbols, StringBuilder
		// stringBuilder, Object object, Object object2) {

		for(TSource item:source){
			func.apply(seed, item);
		}
		return resultSelector.apply(seed);


	}

	public static <TSource, TKey, TResult> List<GroupCollection<TKey, TResult>> groupBy(Iterable<TSource> source,
			Function<TSource, TKey> keySelector, 
			BiFunction<TKey, TSource, TResult> resultSelector) {
		
		List<GroupCollection<TKey, TResult>> list = new ArrayList<GroupCollection<TKey, TResult>>();
		// List<TKey>
		for (TSource item :source) {
			final TKey key = keySelector.apply(item);
			GroupCollection<TKey, TResult> group = firstOrDefault(list, (t) -> t.getKey().equals(key));
			if (group == null) {
				group = new GroupCollection<TKey, TResult>();
				List<TResult> result = new ArrayList<TResult>();

				result.add(resultSelector.apply(key, item));
				group.setItems(result);
				group.setKey(key);
				list.add(group);
			} else {
				List<TResult> result2 = group.getItems();

				result2.add(resultSelector.apply(key, item));
				group.setItems(result2);
			}

		}
		return list;

	}
	
	public static <TSource, TKey> List<GroupCollection<TKey, TSource>>
	groupBy(Iterable<TSource> source,
			Function<TSource, TKey> keySelector) {


		return groupBy(source,keySelector,(t,t2)->t2);

	}

	public static <T> boolean sequeceEqual(List<T> list, List<T> other) {

		if(list==null || other==null)return false;
		if(list.size()!=other.size())return false;
		 int len=list.size();
		 for(int i=0;i<len;++i){
			 if(!list.get(i).equals(other.get(i))){
				 return false;
			 }
		 }
		return true;
	}
	
	public static <T> boolean sequeceEqual(List<T> list, List<T> other,IEqualityComparer<T> comparer) {
		if(list==null || other==null)return false;
		if(list.size()!=other.size())return false;
		 int len=list.size();
		 for(int i=0;i<len;++i){
			 //if(!list.get(i).equals(other.get(i))){
			 if(comparer.equals(list.get(i),other.get(i)) 
					 && 
					 comparer.hashCode(list.get(i))==comparer.hashCode(other.get(i))){
				 return false;
			 }
		 }
		return true;
	}
	
	public static <T> List<T> skip(List<T> source,int count){
		List<T> result=new ArrayList<T>();
		if(count>=source.size())return result;
		int len=source.size();
		for(int i=count-1;i<len;++i){
			result.add(source.get(i));
		}
		return result;
	}

	public static <T> List<T> union(T[] viewLocationsSearched, T[] masterLocationsSearched) {
		// TODO Auto-generated method stub
	List<T> result=Arrays.asList(viewLocationsSearched);
		for(T item :masterLocationsSearched){
			if(!result.stream().anyMatch(p->p.equals(item))){
				result.add(item);
			}
		}
		return result;
	}

}
