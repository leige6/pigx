/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.pig4cloud.pigx.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pigx.admin.api.entity.SysDict;
import com.pig4cloud.pigx.admin.api.entity.SysDictDetail;
import com.pig4cloud.pigx.admin.service.SysDictDetailService;
import com.pig4cloud.pigx.admin.service.SysDictService;
import com.pig4cloud.pigx.common.core.util.R;
import com.pig4cloud.pigx.common.data.annotation.NoRepeatSubmit;
import com.pig4cloud.pigx.common.log.annotation.SysLog;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author lengleng
 * @since 2017-11-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dict")
@Api(value = "dict", description = "字典管理模块")
public class DictController {
	private final SysDictService sysDictService;

	private final SysDictDetailService sysDictDetailService;

	/**
	 * 通过ID查询字典信息
	 *
	 * @param id ID
	 * @return 字典信息
	 */
	@GetMapping("/{id}")
	public R getById(@PathVariable Integer id) {
		return new R<>(sysDictService.getById(id));
	}


	/**
	 * 通过ID查询字典信息
	 *
	 * @param id ID
	 * @return 字典信息
	 */
	@GetMapping("/detail/{id}")
	public R getDetailById(@PathVariable Integer id) {
		return new R<>(sysDictDetailService.getById(id));
	}
	/**
	 * 分页查询字典信息
	 *
	 * @param page 分页对象
	 * @return 分页对象
	 */
	@GetMapping("/page")
	public R<IPage> getDictPage(Page page, SysDict sysDict) {
		QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(sysDict.getName())){
			queryWrapper.like("name",sysDict.getName());
		}
		return new R<>(sysDictService.page(page, queryWrapper));
	}


	/**
	 * 分页查询字典信息
	 *
	 * @return 分页对象
	 */
	@GetMapping("/getDetailList")
	public R<List<SysDictDetail>> getDetailList(Integer dictId,String code) {
		List<SysDictDetail> sysDictDetails=new ArrayList<>();
		if(dictId!=null&&dictId!=0){
			QueryWrapper<SysDictDetail> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("dict_id",dictId);
			sysDictDetails= sysDictDetailService.list(queryWrapper);
		}
		if(StringUtils.isNotBlank(code)){
			sysDictDetails= sysDictDetailService.selectSysDictDetailByCode(code);
		}
		return new R<>(sysDictDetails);
	}


	/**
	 *
	 *
	 * @param
	 * @return 分页对象
	 */
	@GetMapping("/validateCode")
	public R validateCode(String code,Integer id) {
		Boolean flag=true;
		QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(code)){
			queryWrapper.eq("code",code);
		}
		if(id!=0){
			queryWrapper.ne("id",id);
		}
		if(StringUtils.isNotBlank(code) || id!=0){
			List<SysDict> sysDicts= sysDictService.list(queryWrapper);
			flag=sysDicts.size()>0?false:true;
		}
		return new R<>(flag);
	}

	/**
	 * 通过字典类型查找字典
	 *
	 * @param type 类型
	 * @return 同类型字典
	 */
	/*@GetMapping("/type/{type}")
	@Cacheable(value = "dict_details", key = "#type")
	public R getDictByType(@PathVariable String type) {
		return new R<>(sysDictService.list(Wrappers
			.<SysDict>query().lambda()
			.eq(SysDict::getType, type)));
	}*/

	/**
	 * 添加字典
	 *
	 * @param sysDict 字典信息
	 * @return success、false
	 */
	@NoRepeatSubmit
	@SysLog("添加字典")
	@PostMapping
	@CacheEvict(value = "dict_details", key = "#sysDict.code")
	@PreAuthorize("@pms.hasPermission('sys_dict_add')")
	public R save(@Valid @RequestBody SysDict sysDict) {
		return new R<>(sysDictService.save(sysDict));
	}


	/**
	 * 添加字典值
	 *
	 * @param sysDictDetail 字典信息
	 * @return success、false
	 */
	@NoRepeatSubmit
	@SysLog("添加字典值")
	@PostMapping("/detail/save")
	@PreAuthorize("@pms.hasPermission('sys_dict_add')")
	public R save(@Valid @RequestBody SysDictDetail sysDictDetail) {
		return new R<>(sysDictDetailService.save(sysDictDetail));
	}


	/**
	 * 删除字典，并且清除字典缓存
	 *
	 * @param id   ID
	 * @param code 类型
	 * @return R
	 */
	@SysLog("删除字典")
	@DeleteMapping("/{id}/{code}")
	@CacheEvict(value = "dict_details", key = "#code")
	@PreAuthorize("@pms.hasPermission('sys_dict_del')")
	public R removeById(@PathVariable Integer id, @PathVariable String code) {
		return new R<>(sysDictService.removeById(id));
	}

	/**
	 * 删除字典，并且清除字典缓存
	 *
	 * @param id   ID
	 * @return R
	 */
	@SysLog("删除字典")
	@DeleteMapping("/detail/{id}")
	@PreAuthorize("@pms.hasPermission('sys_dict_del')")
	public R removedetailById(@PathVariable Integer id) {
		return new R<>(sysDictDetailService.removeById(id));
	}

	/**
	 * 修改字典
	 *
	 * @param sysDict 字典信息
	 * @return success/false
	 */
	@NoRepeatSubmit
	@PutMapping
	@SysLog("修改字典")
	@CacheEvict(value = "dict_details", key = "#sysDict.code")
	@PreAuthorize("@pms.hasPermission('sys_dict_edit')")
	public R updateById(@Valid @RequestBody SysDict sysDict) {
		return new R<>(sysDictService.updateById(sysDict));
	}


	/**
	 * 修改字典值
	 *
	 * @param sysDictDetail 字典值信息
	 * @return success/false
	 */
	@NoRepeatSubmit
	@PutMapping("/detail/update")
	@SysLog("修改字典")
	@PreAuthorize("@pms.hasPermission('sys_dict_edit')")
	public R updateDetailById(@Valid @RequestBody SysDictDetail sysDictDetail) {
		return new R<>(sysDictDetailService.updateById(sysDictDetail));
	}
}
