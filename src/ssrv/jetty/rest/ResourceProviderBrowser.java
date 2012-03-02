package ssrv.jetty.rest;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import ssrv.resource.Directory;
import ssrv.resource.Resource;
import ssrv.resource.ResourceProvider;
import ssrv.util.Pair;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ResourceProviderBrowser extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private ServerContext context;
	
	public static final String PROVIDER_ID = "pid";
	public static final String RESOURCE_ID = "rid";
	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAILED = "failed";
	public static final String CURRENT_PATH = "path";
	
	public ResourceProviderBrowser(ServerContext _context)
	{
		context = _context;
	}
	
	private Pair<LinkedList<String>,Directory> identifyDirectory(Directory _root,JSONArray _path)
	{
		LinkedList<String> pathList = new LinkedList<String>();
		Set<Directory> dirs = _root.getDirectories();
		Directory pwd = _root;
		for(Object obj : _path){
			String name = obj.toString();
			for(Directory dir : dirs){
				if(dir.getName().equals(name)){
					pwd = dir;
					pathList.addLast(name);
					dirs = dir.getDirectories();
					continue;
				}
			}

			pwd = null;
			break;
		}
		
		return new Pair<LinkedList<String>,Directory>(pathList,pwd);
	}
	
	private void addResourcesAndDirectoriesToJSONObject(Directory _dir,LinkedList<String> _pathToDir,JSONObject _obj)
	{
		//add Directories
		HashMap<String,String> directoryMap = new HashMap<String,String>();
		for(Directory child : _dir.getDirectories()){
			_pathToDir.addLast(child.getName());
			JSONObject pathJSONObj = new JSONObject();
			pathJSONObj.put(CURRENT_PATH,_pathToDir);
			String pathJSONStr = pathJSONObj.toString();
			String pathJSONStrBase64 = Base64.encodeBase64URLSafeString(pathJSONStr.getBytes());
			
			directoryMap.put(child.getName(),pathJSONStrBase64);
			_pathToDir.removeLast();
		}
		
		//add Resources
		HashMap<String,String> resourceMap = new HashMap<String,String>();
		for(Resource resource : _dir.getResources()){
			resourceMap.put(resource.getName(),resource.getID());
		}
		
		//concat into json
		_obj.put("directories",directoryMap);
		_obj.put("resources",resourceMap);
	}
	
	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		/*
		 * get parameter PROVIDER_ID and CURRENT_PATH
		 * PROVIDER_ID is an integer
		 * CURRENT_PATH is base64-encoded JSON string , it contains path as an array
		 * ex)
		 * {
		 *   "path":["aaa","bbb","ccc"]
		 * }
		 */
		String pid = _req.getParameter(PROVIDER_ID);
		String pathBase64 = _req.getParameter(CURRENT_PATH);
		
		//
		boolean flagError = false;
		
		JSONObject json = new JSONObject();
		if(pid == null || pid.length() == 0){
			//invalid pid , pid is null or length of pid is 0
			json.put(STATUS,STATUS_FAILED);
			json.put(MESSAGE,"invalid pid");
			flagError = true;
		}
		
		//get ResourceProvider identified by pid.
		ResourceProvider provider = context.getResourceProviders().get(pid);
		if(provider == null){
			json.put(STATUS,STATUS_FAILED);
			json.put(MESSAGE,"invalid pid");
			flagError = true;
		}
		
		if(!flagError){
			//prepare for response JSON-string.
			JSONArray path = null;
			
			//the pathBase64(= CURRENT_PATH) can be null when browse root directory.
			if(pathBase64 != null){
				//if not null , decode pathBase64 to JSON string translate to JSONArray
				String pathJSONStr = new String(Base64.decodeBase64(pathBase64.getBytes()));
				JSONObject pathJSON = JSONObject.fromObject(pathJSONStr);
				path = pathJSON.getJSONArray("path");
			}else{
				//pathBase64 is null , create empty JSONArray
				path = new JSONArray();
			}

			//identify the current directory.
			Pair<LinkedList<String>,Directory> result = identifyDirectory(provider.getRootDirectory(),path);
			Directory pwd = result.right;
			LinkedList<String> pathList = result.left;
			
			if(pwd == null){
				json.put(STATUS,STATUS_FAILED);
				json.put(MESSAGE,"invalid path");
			}else{
				json.put(STATUS,STATUS_SUCCESS);
				json.put(MESSAGE,"list of resources.");
				addResourcesAndDirectoriesToJSONObject(pwd,pathList,json);
			}
		}
		
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(_res.getOutputStream());
		}catch(Exception _e){
			_e.printStackTrace();
			_res.setStatus(500);
		}
		
		json.write(pw);
		pw.flush();
	}
}
