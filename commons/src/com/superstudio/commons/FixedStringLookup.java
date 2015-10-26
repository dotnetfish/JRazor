package com.superstudio.commons;

import com.superstudio.commons.csharpbridge.RefObject;

public class FixedStringLookup
{
	public static boolean Contains(String[][] lookupTable, String value, boolean ignoreCase)
	{
		int length = value.length();
		if (length <= 0 || length - 1 >= lookupTable.length)
		{
			return false;
		}
		String[] array = lookupTable[length - 1];
		return array != null && FixedStringLookup.Contains(array, value, ignoreCase);
	}
	private static boolean Contains(String[] array, String value, boolean ignoreCase)
	{
		int num = 0;
		int num2 = array.length;
		int i = 0;
		while (i < value.length())
		{
			char c;
			if (ignoreCase)
			{
				c =Character.toLowerCase(value.charAt(i)); //Character.ToLower(value.charAt(i));
			}
			else
			{
				c = value.charAt(i);
			}
			if (num2 - num <= 1)
			{
				if (c != array[num].charAt(i))
				{
					return false;
				}
				i++;
			}
			else
			{
				RefObject<Integer> refNum=new RefObject<Integer>(num);
				RefObject<Integer> refNum2=new RefObject<Integer>(num2);
				if (!FixedStringLookup.FindCharacter(array, c, i, refNum, refNum2))
				{
					num=refNum.getRefObj();
					num2=refNum2.getRefObj();
					return false;
				}
				num=refNum.getRefObj();
				num2=refNum2.getRefObj();
				i++;
			}
		}
		return true;
	}
	private static boolean FindCharacter(String[] array, char value, int pos, RefObject<Integer> minRef, RefObject<Integer>  maxRef)
	{
		int num = minRef.getRefObj();
		int max=maxRef.getRefObj();
		int min=minRef.getRefObj();
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
				maxRef.setRefObj(max);
				minRef.setRefObj(min);
				return true;
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
		maxRef.setRefObj(max);
		minRef.setRefObj(min);
		return false;
	}
}

