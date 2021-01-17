/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import org.eclipse.jetty.http.HttpStatus;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static org.magnum.mobilecloud.video.client.VideoSvcApi.*;

@Controller
public class VideoController {
	
	/**
	 * Author : Sina Pouya
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */
	@Autowired
	VideoRepository videoRepository;

	@RequestMapping(value="/go",method=RequestMethod.GET)
	public @ResponseBody String goodLuck(){
		return "Good Luck!";
	}

	@RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.POST)
	public @ResponseBody Video  addVideo(@RequestBody Video video){
		return videoRepository.save(video);
	}
	@RequestMapping(value = VIDEO_SVC_PATH,method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList() {
		Collection<Video> videos = new ArrayList<>();
		Iterable<Video> videoIterator =  videoRepository.findAll();
		Iterator<Video> iterator = videoIterator.iterator();
		while (iterator.hasNext()){
			Video video = iterator.next();
			videos.add(video);
		}
		return videos;
	}
	@RequestMapping(value = VIDEO_SVC_PATH + "/{id}",method = RequestMethod.GET)
	public @ResponseBody Video getVideo(@PathVariable Long id){
		return videoRepository.findOne(id);
	}
	@RequestMapping(value = VIDEO_SVC_PATH + "/{id}/like" ,method = RequestMethod.POST)
	@Transactional
	public void likeVideo(@PathVariable long id, Principal principal, HttpServletResponse httpResponse) throws IOException {
		if(!videoRepository.exists(id))
			httpResponse.sendError(HttpStatus.NOT_FOUND_404);

		Video v = videoRepository.findOne(id);
		Set<String> likedBy = v.getLikedBy();
		if(!likedBy.contains(principal.getName()))
		{
			likedBy.add(principal.getName());
			v.setLikes(v.getLikes()+1);
			videoRepository.save(v);
			httpResponse.setStatus(HttpStatus.OK_200);
		}
		else
			httpResponse.setStatus(HttpStatus.BAD_REQUEST_400);
	}
	@RequestMapping(value = VIDEO_SVC_PATH + "/{id}/unlike" ,method = RequestMethod.POST)
	@Transactional
	public void unLikeVideo(@PathVariable long id, Principal principal, HttpServletResponse httpResponse) throws IOException {
		if(!videoRepository.exists(id))
			httpResponse.sendError(HttpStatus.NOT_FOUND_404);

		Video v = videoRepository.findOne(id);
		Set<String> likedBy = v.getLikedBy();
		if(likedBy.contains(principal.getName()))
		{
			likedBy.remove(principal.getName());
			v.setLikes(v.getLikes()-1);
			videoRepository.save(v);
			httpResponse.setStatus(HttpStatus.OK_200);
		}
		else
			httpResponse.setStatus(HttpStatus.BAD_REQUEST_400);
	}
	@RequestMapping(value = VIDEO_TITLE_SEARCH_PATH,method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(@RequestParam String title){
		return videoRepository.findVideosByName(title);
	}

	@RequestMapping(value = VIDEO_DURATION_SEARCH_PATH,method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(long duration){
		return videoRepository.findVideosByDurationLessThan(duration);
	}
}
