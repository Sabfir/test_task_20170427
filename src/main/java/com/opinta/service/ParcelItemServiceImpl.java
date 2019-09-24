package com.opinta.service;

import com.opinta.dao.ParcelItemDao;
import com.opinta.entity.ParcelItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class ParcelItemServiceImpl implements ParcelItemService {
    private final ParcelItemDao parcelItemDao;

    @Autowired
    public ParcelItemServiceImpl(ParcelItemDao parcelItemDao) {
        this.parcelItemDao = parcelItemDao;
    }

    @Transactional
    @Override
    public List<ParcelItem> getAllEntities() {
        log.info("Getting all parcelItems");
        return parcelItemDao.getAll();
    }

    @Transactional
    @Override
    public ParcelItem getEntityById(long id) {
        log.info("Getting parcelItem by id {}", id);
        return parcelItemDao.getById(id);
    }

    @Transactional
    @Override
    public ParcelItem saveEntity(ParcelItem parcelItem) {
        log.info("Saving parcelItem {}", parcelItem);
        return parcelItemDao.save(parcelItem);
    }

    @Transactional
    @Override
    public ParcelItem updateEntity(long id, ParcelItem parcelItem) {
        ParcelItem target = parcelItemDao.getById(id);
        if (target == null) {
            log.debug("Can't update parcelItem. Doesn't exist {}", id);
            return null;
        }
        log.info("Updating parcelItem {}", parcelItem);
        parcelItem.setId(id);
        parcelItemDao.update(parcelItem);
        return parcelItem;
    }

    @Transactional
    @Override
    public boolean delete(long id) {
        ParcelItem target = parcelItemDao.getById(id);
        if (target == null) {
            log.debug("Can't delete parcelItem, it doesn't exists {}", id);
            return false;
        }
        log.info("Deleting parcelItem {}", target);
        parcelItemDao.delete(target);
        return true;
    }
}
