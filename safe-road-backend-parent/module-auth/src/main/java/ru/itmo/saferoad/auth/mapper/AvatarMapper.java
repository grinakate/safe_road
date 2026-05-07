package ru.itmo.saferoad.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.saferoad.auth.dto.avatar.AvatarCreateRequest;
import ru.itmo.saferoad.auth.dto.avatar.AvatarResponse;
import ru.itmo.saferoad.auth.domain.Avatar;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvatarMapper {

	@Mapping(target = "id", ignore = true)
	Avatar mapToEntity(AvatarCreateRequest request);

	AvatarResponse mapToResponse(Avatar avatar);

	List<AvatarResponse> mapToResponse(List<Avatar> avatars);

}
