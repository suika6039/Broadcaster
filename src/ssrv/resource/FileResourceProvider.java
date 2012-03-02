package ssrv.resource;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.mortbay.util.ajax.JSONObjectConvertor;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.test.JSONAssert;
import net.sf.json.util.JSONStringer;

public class FileResourceProvider implements ResourceProvider
{
	public static void main(String _args[]) throws Exception
	{
		File root = new File("./src");
		FileResourceProvider provider = new FileResourceProvider(root);
		
		traverse(provider.getRootDirectory(),"+");
	}

	@Override
	public Resource resolve(String _id)
	{
		String jsonStr = new String(Base64.decodeBase64(_id));
		
		JSONObject json = null;
		try{
			json = JSONObject.fromObject(jsonStr);
		}catch(JSONException _e){
			_e.printStackTrace();
			return null;
		}
		
		JSONArray path = json.getJSONArray("path");
		
		Resource target = null;
		Directory pwd = this.getRootDirectory();
		for(Object nameObj : path){
			String name = nameObj.toString();
			for(Directory dir : pwd.getDirectories()){
				if(dir.getName().equals(name)){
					pwd = dir;
					continue;
				}
			}
			
			for(Resource res : pwd.getResources()){
				if(res.getName().equals(name)){
					target = res;
					break;
				}
			}
			
			break;
		}
		
		return target;
	}
	
	public static void traverse(Directory _parent,String _indent)
	{
		System.out.println(_indent + _parent.getName());
		
		for(Directory dir : _parent.getDirectories()){
			traverse(dir,_indent + _indent);
		}
		
		for(Resource res : _parent.getResources()){
			System.out.println(_indent + _indent + res.getName());
		}
	}
	
	private File rootDir;
	
	public FileResourceProvider(File _dir)
	{
		if(!_dir.isDirectory()){
			throw new IllegalArgumentException("_dir is not directory");
		}
		
		rootDir = _dir;
	}
	
	@Override
	public String getName()
	{
		return rootDir.getName();
	}
	
	@Override
	public Directory getRootDirectory()
	{
		return new DirectoryImpl(rootDir,null);
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
	
	@Override
	public int hashCode()
	{
		return 17 + 37*rootDir.hashCode();
	}
	
	@Override
	public boolean equals(Object _obj)
	{
		if(_obj instanceof FileResourceProvider){
			if(_obj == this){
				return true;
			}
			
			FileResourceProvider provider = (FileResourceProvider)_obj;
			return this.rootDir.equals(provider.rootDir);
		}
		
		return false;
	}
	
	public static class ResourceImpl implements Resource
	{
		private final File file;
		private final Directory parent;
		private final String id;
		
		public ResourceImpl(File _file,Directory _parent,String _id)
		{
			file = _file;
			parent = _parent;
			id = _id;
		}
		
		@Override
		public String getID()
		{
			return id;
		}
		
		@Override
		public String getName()
		{
			String name = file.getName();
			return name;
		}
		
		@Override
		public Directory getParent()
		{
			return parent;
		}

		@Override
		public InputStream getInputStream() throws IOException
		{
			return new FileInputStream(file);
		}
	}
	
	public static class DirectoryImpl implements Directory
	{
		private final File pwd;
		private final Directory parent;
		
		private static final FileFilter directoryFilter = new FileFilter(){
			public boolean accept(File _f)
			{
				return _f.isDirectory();
			}
		};
		
		private static final FileFilter fileFilter = new FileFilter(){
			public boolean accept(File _f)
			{
				return !_f.isDirectory();
			}
		};
		
		public DirectoryImpl(File _dir,Directory _parent)
		{
			if(!_dir.isDirectory()){
				throw new IllegalArgumentException("_dir is not a directory.");
			}
			
			parent = _parent;
			pwd = _dir;
		}
		
		@Override
		public String getName()
		{
			String name = pwd.getName();
			return name;
		}

		@Override
		public Directory getParent()
		{
			return parent;
		}
		
		private Set<Directory> createDirectories()
		{
			HashSet<Directory> directories = new HashSet<Directory>();
			File[] files = pwd.listFiles(directoryFilter);
			for(File dir : files){
				Directory directory = new DirectoryImpl(dir,this);
				directories.add(directory);
			}
			
			return directories;
		}
		
		private Set<Resource> createResources()
		{
			HashSet<Resource> resources = new HashSet<Resource>();
			
			File[] files = pwd.listFiles(fileFilter);
			
			Directory p = this;
			LinkedList<String> list = new LinkedList<String>();
			while(p != null){
				list.addFirst(p.getName());
				p = p.getParent();
			}
			list.removeFirst();
			
			//build json
			
			for(File file : files){
				list.addLast(file.getName());
				JSONObject json = new JSONObject();
				json.put("path",list);
				String jsonStr = json.toString();
				String jsonStrBase64 = Base64.encodeBase64URLSafeString(jsonStr.getBytes());
				Resource res = new ResourceImpl(file,this,jsonStrBase64);
				resources.add(res);
				
				list.removeLast();
			}
			
			return resources;
		}

		@Override
		public Set<Resource> getResources()
		{
			return createResources();
		}

		@Override
		public Set<Directory> getDirectories()
		{
			return createDirectories();
		}
	}

}
