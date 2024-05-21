package com.beple.ex_2fa.mapper;

import com.beple.ex_2fa.domain.user.User;
import com.beple.ex_2fa.payload.user.UserRes;
import com.beple.ex_2fa.payload.user.UserUpdateReq;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserRes toRes(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UserUpdateReq req,@MappingTarget User user);
}
