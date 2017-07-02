package com.superstudio.commons;

import com.superstudio.codedom.compiler.MutilReturn;

public class FixedStringLookup
{
	public static boolean contains(String[][] lookupTable, String value, boolean ignoreCase)
	{
		int length = value.length();
		if (length <= 0 || length - 1 >= lookupTable.length)
		{
			return false;
		}
		String[] array = lookupTable[length - 1];
		return array != null && FixedStringLookup.contains(array, value, ignoreCase);
	}
	private static boolean contains(String[] array, String value, boolean ignoreCase)
	{
		int indexNum = 0;
		int length = array.length;
		int i = 0;
		//if(ignoreCase)value=value.toLowerCase();
		while (i < value.length())
		{
			char c =value.charAt(i);
			if (ignoreCase)
			{
				c =Character.toLowerCase(c); //Character.ToLower(value.charAt(i));
			}

			if (length - indexNum <= 1)
			{
				if (c != array[indexNum].charAt(i))
				{
					return false;
				}
				i++;
			}
			else
			{
                MutilReturn<Boolean,MutilReturn<Integer,Integer>> findResult=FixedStringLookup.findCharacter(array,c,i,indexNum,length);
                indexNum=findResult.gettRef().gettReturn();
                length=findResult.gettRef().gettRef();

				if(!findResult.gettReturn())
				{

                    return false;
				}
				i++;
			}
		}
		return true;
	}

	private static MutilReturn<Boolean,MutilReturn<Integer,Integer>> findCharacter(String[] array,
																				   char value,
																				   int pos,
																				   Integer minRef,
																				   Integer maxRef)
	{
		int num = minRef;
		int max=maxRef;
		int min=minRef;
		while (min < max)
		{
			num = (min + max) / 2;
			char c = array[num].charAt(pos);
			if (value == c)
			{
				int num2 = num;
				while (num2 > min && array[num2 - 1].charAt(pos) == value)
				{
					num2--;
				}
				min = num2;
				int num3 = num + 1;
				while (num3 < max && array[num3].charAt(pos) == value)
				{
					num3++;
				}
				max = num3;
				return MutilReturn.Return(true,MutilReturn.Return(max,min));
            }
			if (value < c)
			{
				max = num;
			}
			else
			{
				min = num + 1;
			}
		}
		return MutilReturn.Return(false,MutilReturn.Return(max,min));

	}

}

