package com.qprogramming.tasq.signin;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

public class PersistentTokenRepositoryImpl implements PersistentTokenRepository {

	private final RememberMeTokenRepository rememberMeTokenRepository;

	public PersistentTokenRepositoryImpl(RememberMeTokenRepository rememberMeTokenRepository) {
		this.rememberMeTokenRepository = rememberMeTokenRepository;
	}

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		RememberMeToken newToken = new RememberMeToken(token);
		if (newToken != null) {
			this.rememberMeTokenRepository.save(newToken);
		}
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		RememberMeToken token = this.rememberMeTokenRepository.findBySeries(series);
		if (token != null) {
			token.setTokenValue(tokenValue);
			token.setDate(lastUsed);
			this.rememberMeTokenRepository.save(token);
		}

	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		RememberMeToken token = this.rememberMeTokenRepository.findBySeries(seriesId);
		if (token != null) {
			return new PersistentRememberMeToken(token.getUsername(), token.getSeries(), token.getTokenValue(),
					token.getDate());
		} else
			return null;
	}

	@Override
	public void removeUserTokens(String username) {
		List<RememberMeToken> tokens = this.rememberMeTokenRepository.findByUsername(username);
		if (tokens != null) {
			this.rememberMeTokenRepository.delete(tokens);
		}
	}

}
