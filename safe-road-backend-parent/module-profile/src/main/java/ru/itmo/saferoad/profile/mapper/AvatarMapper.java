package ru.itmo.saferoad.profile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.saferoad.core.dto.profile.avatar.AvatarCreateRequest;
import ru.itmo.saferoad.core.dto.profile.avatar.AvatarResponse;
import ru.itmo.saferoad.profile.domain.Avatar;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvatarMapper {

	@Mapping(target = "id", ignore = true)
	Avatar mapToEntity(AvatarCreateRequest request);

	AvatarResponse mapToResponse(Avatar avatar);

	List<AvatarResponse> mapToResponse(List<Avatar> avatars);

}
