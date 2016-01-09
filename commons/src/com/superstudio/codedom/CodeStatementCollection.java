package com.superstudio.codedom;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeStatementCollection : CollectionBase
public class CodeStatementCollection extends CollectionBase implements Serializable
{
	public final CodeStatement getItem(int index)
	{
		return (CodeStatement)get(index);
	}
	public final void setItem(int index, CodeStatement value)
	{
		add(index,value);
	}

	public CodeStatementCollection()
	{
	}

	public CodeStatementCollection(CodeStatementCollection value)
	{
		this.addAll(value);
	}

	public CodeStatementCollection(CodeStatement[] value)
	{
		this.addAll(value);
	}

	public final int Add(CodeStatement value)
	{
		add(value);return size();
	}

	public final int Add(CodeExpression value)
	{
		return this.Add(new CodeExpressionStatement(value));
	}

	public final void addAll(CodeStatement[] value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("value");
		}
		for (int i = 0; i < value.length; i++)
		{
			this.Add(value[i]);
		}
	}

	public final void addAll(CodeStatementCollection value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("value");
		}
		int count = value.size();
		for (int i = 0; i < count; i++)
		{
			this.Add(value.getItem(i));
		}
	}

	public final boolean Contains(CodeStatement value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeStatement[] array, int index)
	{
		subList(index,size()-index-1).toArray(array);
	}

	/*public final int indexOf(CodeStatement value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeStatement value)
	{
		add(index,value);
	}

	public final void Remove(CodeStatement value)
	{
		remove(value);
	}
	/*@Override
	public boolean add(Object e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void add(int index, Object element) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean addAll(int index, Collection c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Object get(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return this.;
	}
	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public ListIterator listIterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ListIterator listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Object remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Object set(int index, Object element) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object[] toArray(Object[] a) {
		// TODO Auto-generated method stub
		return null;
	}*/
}