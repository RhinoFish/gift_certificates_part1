package com.epam.esm.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.epam.esm.dto.GiftDTO;
import com.epam.esm.dto.TagDTO;
import com.epam.esm.model.Gift;
import com.epam.esm.model.Tag;
import com.epam.esm.service.GiftService;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	@Autowired
	private final TagService tagService;
	@Autowired
	private final GiftService giftService;

	public HomeController(TagService tagService, GiftService giftService) {
		this.tagService = tagService;
		this.giftService = giftService;
	}


	@RequestMapping(value="/")
	public ModelAndView test(HttpServletResponse response) throws IOException{
		return new ModelAndView("home");
	}



	@PostMapping(value = "/gifts")
	@ResponseBody
	public ResponseEntity<?> postGift(@RequestBody(required = false) GiftDTO giftDTO) throws SQLException {
		Map<String,Object> response = new HashMap<>();
		if(giftDTO == null){
			response.put("Message","Request body is missing");
			return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
		}
		giftDTO.setCreateDate();
		giftDTO.setLastUpdateDate();

		try{
			giftService.createGift(giftDTO);
			response.put("Message","The gift was created successfully!!");
		}catch (SQLException e){
			response.put("Message", "A error occurred in the creation of the Gift: " + giftDTO.getName());
			response.put("Error", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(response,HttpStatus.CREATED);

	}

	@GetMapping(value = "/gifts")
	@ResponseBody
	public ResponseEntity<?> getGifts(@RequestParam(defaultValue = "name",required = false)String sort,
								  @RequestParam(defaultValue = "",required = false) String tagName,
								  @RequestParam(defaultValue = "",required = false) String nameDesc,
								  @RequestParam(defaultValue = "true",required = false) boolean ascending){
		Map<String,Object> response = new HashMap<>();
		List<GiftDTO> gifts;
		try{
			gifts = giftService.getAllGifts(sort,tagName,nameDesc,ascending);
			response.put("Gifts", gifts);
			return new ResponseEntity<>(response,HttpStatus.OK);
		}catch (SQLException e){
			response.put("Message","There was an error getting the gifts");
			response.put("Error", e.getMessage());
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping(value = "/gifts/{giftId}")
	@ResponseBody
	public ResponseEntity<?> putGift(@RequestBody GiftDTO giftDTO,@PathVariable int giftId) throws SQLException {
		Map<String,Object> response = new HashMap<>();
		GiftDTO actualGift;
		if(giftDTO == null){
			response.put("Message","Request body is missing");
			return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
		}
		try{
			actualGift = giftService.getGift(giftId);
			if(actualGift !=null){
				actualGift.setName(giftDTO.getName());
				actualGift.setDescription(giftDTO.getDescription());
				actualGift.setPrice(giftDTO.getPrice());
				actualGift.setDuration(giftDTO.getDuration());
				actualGift.setLastUpdateDate();
				actualGift.setTags(giftDTO.getTags());
				giftService.updateGift(actualGift);
				response.put("Message", "The gift has been updated!");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}else{
				 response.put("Message","The gift with the id: " + giftId + " doesn't exist");
				 return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
			}
		}catch (SQLException e){
			response.put("Message", "A error ocurred  with the update of the gift: " + giftDTO.getName());
			response.put("Error", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping(value = "/gifts/{giftId}")
	@ResponseBody
	public ResponseEntity<?> deleteGift(@PathVariable int giftId){
		Map<String,Object> response = new HashMap<>();
		Gift gift = giftService.delete(giftId);
		if(gift !=null){
			response.put("Message",gift);
		}else{
			response.put("Message","The Gift with the id " + giftId + " doesn't exist");
			return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

	@PostMapping(value = "/tags")
	@ResponseBody
	public ResponseEntity<?> postTag(@RequestBody(required = false) TagDTO tagDTO){
		Map<String,Object> response  = new HashMap<>();
		if(tagDTO == null){
			response.put("Message","Request body is missing");
			return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
		}
		try{
			tagService.createTag(tagDTO);
			response.put("Message","The tag was created successfully!!");
		}catch (SQLException e){
			response.put("Message","A error occurred in the creation of the Tag: " + tagDTO.getName());
			response.put("Error",e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(response,HttpStatus.CREATED);
	}


	@GetMapping(value = "/tags")
	@ResponseBody
	public List<TagDTO>  getAllTags(){
		return tagService.getAllTags();
	}

	@GetMapping(value = "/tags/{tag_name}")
	@ResponseBody
	public ResponseEntity<?>  getTag(@PathVariable String tag_name){
		Map<String,Object> response = new HashMap<>();

		TagDTO tag = tagService.getTag(tag_name);
		if(tag != null){
			response.put("Message",tag);
		}else{
			response.put("Message","The tag with the name " + tag_name + " doesn't exist");
			return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

	@DeleteMapping(value = "/tags/{tag_name}")
	@ResponseBody
	public ResponseEntity<?> deleteTag(@PathVariable String tag_name){
		Map<String,Object> response = new HashMap<>();
		TagDTO tag = tagService.deleteTag(tag_name);
		if(tag != null){
			response.put("Message","The "+tag.getName()+" tag has been deleted");
		}else{
			response.put("Message","The tag with the name " + tag_name + " doesn't exist");
			return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

}
