package module.dao.iface;

import java.util.List;

import module.dao.BaseBo;

/*
 * This class use to custom fetch lazy object or list
 * @author by kan
 */
public abstract class CustomLazyHandler<T extends BaseBo> {
	
	/*
	 * if result is list
	 */
	public void LazyList(List<T> list)
	{
		return;
	}

	/*
	 * if result is single object
	 */
	public void LazyObject(T obj)
	{
		return;
	}
}
