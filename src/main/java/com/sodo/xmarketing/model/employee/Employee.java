package com.sodo.xmarketing.model.employee;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mongodb.core.mapping.Document;

import com.querydsl.core.annotations.QueryEntity;
import com.sodo.xmarketing.model.account.Account;
import com.sodo.xmarketing.model.config.Format;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@QueryEntity
@Document(collection = "employee")
public class Employee extends Account{
	@NotNull
	@NotEmpty
	String name;
	
	String phone;
	
	String address;
	
	Format format;
}
