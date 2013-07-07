package com.shejiaomao.core.api;

import com.shejiaomao.core.LibException;
import com.shejiaomao.core.entity.User;


public interface SocialService {

	User showUser() throws LibException;
}
