package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.engine.api.vo.engine.EngineSupportVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Deprecated
public class EngineService {

    @Autowired
    private ComponentService componentService;

    public List<QueueVO> getQueue(Long engineId){
       /* List<Queue> queueList = queueDao.listByEngineId(engineId);
        return QueueVO.toVOs(queueList);*/
        return null;
    }

    /**
     * [
     *     {
     *         "engineType":1,
     *         "supportComponent":[1,3,4]
     *     }
     * ]
     */
    public List<EngineSupportVO> listSupportEngine(Long dtUicTenantId,Boolean needCommon){
        List<EngineSupportVO> vos = Lists.newArrayList();
        List<Component> components = componentService.listComponents(dtUicTenantId,null);
        if(CollectionUtils.isEmpty(components)){
            return vos;
        }
        Optional<Component> hasHadoop = components.stream()
                .filter(c -> EComponentType.YARN.getTypeCode().equals(c.getComponentTypeCode()) || EComponentType.KUBERNETES.getTypeCode().equals(c.getComponentTypeCode()))
                .findFirst();
        Map<Integer, List<Component>> engineComponentMapping = components.stream().collect(Collectors.groupingBy(c -> {
                    EComponentType componentType = EComponentType.getByCode(c.getComponentTypeCode());
                    MultiEngineType engineTypeByComponent = EComponentType.getEngineTypeByComponent(componentType,c.getDeployType());
                    if (null != engineTypeByComponent) {
                        return engineTypeByComponent.getType();
                    }
                    //兼容旧逻辑
                    return hasHadoop.isPresent() ? MultiEngineType.HADOOP.getType() : 0;
                }
                , Collectors.mapping(component -> component, Collectors.toList())
        ));

        for (Integer engineType : engineComponentMapping.keySet()) {
            if (0 == engineType) {
                continue;
            }
            if (BooleanUtils.isFalse(needCommon) && MultiEngineType.COMMON.getType() == engineType) {
                continue;
            }
            EngineSupportVO engineSupportVO = new EngineSupportVO();
            engineSupportVO.setEngineType(engineType);
            List<Component> componentList = engineComponentMapping.get(engineType);
            if (CollectionUtils.isEmpty(componentList)) {
                continue;
            }

            List<Integer> componentTypes = componentList.stream()
                    .map(Component::getComponentTypeCode)
                    .collect(Collectors.toList());
            engineSupportVO.setSupportComponent(componentTypes);
            Optional<Component> metadataComponent = componentList.stream()
                    .filter(c -> null != c.getIsMetadata() && 1 == c.getIsMetadata())
                    .findFirst();
            metadataComponent.ifPresent(component -> engineSupportVO.setMetadataComponent(component.getComponentTypeCode()));
            vos.add(engineSupportVO);
        }
        return vos;
    }
}

