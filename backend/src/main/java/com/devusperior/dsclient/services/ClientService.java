package com.devusperior.dsclient.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devusperior.dsclient.dto.ClientDTO;
import com.devusperior.dsclient.entities.Client;
import com.devusperior.dsclient.repositories.ClientRepository;
import com.devusperior.dsclient.services.exceptions.DatabaseException;
import com.devusperior.dsclient.services.exceptions.ResourceNotFoundException;

@Service
public class ClientService {

	@Autowired 
	private ClientRepository repository;
	
	@Transactional(readOnly = true)
	public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
		Page<Client> listPage = repository.findAll(pageRequest);
		return listPage.map(mp -> new ClientDTO(mp));
	}

	@Transactional(readOnly = true)
	public ClientDTO findById(Long id) {
		Optional<Client> obj = repository.findById(id);
		Client entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not Found"));
		return new ClientDTO(entity);
	}

	@Transactional(readOnly = false)
	public ClientDTO insertClient(ClientDTO dto) {
		Client entity = new Client();
		copyDtoToEntit(dto, entity);
		entity = repository.save(entity);
		return new ClientDTO(entity);
	}



	@Transactional(readOnly = false)
	public ClientDTO updateClient(Long id, ClientDTO dto) {
		try {
			Client entity = repository.getOne(id);
			copyDtoToEntit(dto, entity);
			entity = repository.save(entity);
			return new ClientDTO(entity);

		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("ID Not Found " + id);
		}

	}


	public void deleteClient(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("ID Not Found " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity Violation - ID in Use " + id);
		}
		
	}
	
	private void copyDtoToEntit(ClientDTO dto, Client entity) {
		
		entity.setBirthDate(dto.getBirthDate());
		entity.setChildren(dto.getChildren());
		entity.setCpf(dto.getCpf());
		entity.setIncome(dto.getIncome());
		entity.setName(dto.getName());
		
	}
}
