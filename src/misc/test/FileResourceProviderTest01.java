package misc.test;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.JUnitCore;

import ssrv.resource.Directory;
import ssrv.resource.FileResourceProvider;
import ssrv.resource.Resource;

public class FileResourceProviderTest01
{
	private FileResourceProvider provider;
	private File root;
	
	public static final FileFilter directoryFilter = new FileFilter(){
		@Override
		public boolean accept(File _f)
		{
			return _f.isDirectory();
		}
	};
	
	public static final FileFilter fileFilter = new FileFilter(){
		@Override
		public boolean accept(File _f)
		{
			return !_f.isDirectory();
		}
	};
	
	public static void main(String[] _args)
	{
		JUnitCore.main(FileResourceProviderTest01.class.getName());
	}
	
	public FileResourceProviderTest01()
	{
		root = new File("./test");
		provider = new FileResourceProvider(root);
	}
	
	@Test
	public void testRootHasCorrectDirectories()
	{
		Directory root = provider.getRootDirectory();
		Set<Directory> directories = root.getDirectories();
		File[] files = this.root.listFiles(directoryFilter);
		
		for(File file : files){
			String name = file.getName();
			boolean find = false;
			for(Directory dir : directories){
				if(name.equals(dir.getName())){
					find = true;
				}
			}
			
			if(!find){
				Assert.fail(name+" was not found in this directory [directory = "+root.getName()+"]");
			}
		}
	}
	
	@Test
	public void testRootHasCorrectFiles()
	{
		Directory root = provider.getRootDirectory();
		Set<Resource> resources = root.getResources();
		File[] files = this.root.listFiles(fileFilter);
		
		for(File file : files){
			String name = file.getName();
			boolean find = false;
			for(Resource res : resources){
				if(name.equals(res.getName())){
					find = true;
				}
			}
			
			if(!find){
				Assert.fail(name+" was not found in this directory [resource = "+root.getName()+"]");
			}
		}
	}
	
	@Test
	public void testRootIsNotNull()
	{
		Directory root = provider.getRootDirectory();
		Assert.assertNotNull(root);
	}
	
	@Test
	public void testRootHasNullParent()
	{
		Directory root = provider.getRootDirectory();
		Directory parent = root.getParent();
		Assert.assertNull(parent);
	}
	
	@Test
	public void testRootNameIsCorrect()
	{
		String name = "test";
		Directory root = provider.getRootDirectory();
		Assert.assertEquals(name,root.getName());
	}
}
