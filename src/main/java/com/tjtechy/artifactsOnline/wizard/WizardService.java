package com.tjtechy.artifactsOnline.wizard;

import com.tjtechy.artifactsOnline.artifact.utils.IdWorker;
import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

  private final WizardRepository wizardRepository;

  public WizardService(WizardRepository wizardRepository) {

    this.wizardRepository = wizardRepository;
  }
  public Wizard findById(Integer wizardId){

    return this.wizardRepository.findById(wizardId)
            .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
  }

  public List<Wizard> findAll(){

    return this.wizardRepository.findAll();
  }

  public Wizard create(Wizard newWizard){

    return this.wizardRepository.save(newWizard);
  }

  public Wizard update(Integer wizardId, Wizard update){
    //find by id
    //modify
    //not, found, throw exception
    return this.wizardRepository.findById(wizardId)
            .map(oldWizard -> {
              oldWizard.setName(update.getName());
              return this.wizardRepository.save(oldWizard);
            })
            .orElseThrow(()->new ObjectNotFoundException("wizard", wizardId));
  }

  public void delete(Integer wizardId){
    //first find if Id exist or not, then delete
    this.wizardRepository.findById(wizardId).orElseThrow(()-> new ObjectNotFoundException("wizard", wizardId));
    //if found
    this.wizardRepository.deleteById(wizardId);
  }

}
