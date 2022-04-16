package com.cs.wit.proxy;

import com.cs.wit.model.Organ;
import com.cs.wit.persistence.repository.OrganRepository;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganProxy {

    @NonNull
    private final OrganRepository organRes;

    /**
     * 检查组织机构树
     */
    private boolean checkParentOrgan(Organ organ, String organId, String orgi) {
        if (StringUtils.equals(organ.getParent(), "0")) {
            return true;
        }

        if (StringUtils.equals(organ.getId(), organ.getParent())) {
            return false;
        }

        Organ parent = organRes.findByIdAndOrgi(organ.getParent(), orgi);
        if (parent == null) {
            return false;
        } else {
            if (StringUtils.equals(parent.getParent(), organId)) {
                return false;
            } else {
                return checkParentOrgan(parent, organId, orgi);
            }
        }
    }


    public String updateOrgan(final Organ organ, final String orgi) {
        final Organ oldOrgan = organRes.findByNameAndOrgi(organ.getName(), orgi);

        String msg = "admin_organ_update_success";

        if (oldOrgan != null && !StringUtils.equals(oldOrgan.getId(), (organ.getId()))) {
            return "admin_organ_update_name_not";
        }

        if (!checkParentOrgan(organ, organ.getId(), orgi)) {
            return "admin_organ_update_not_standard";
        }

        Organ tempOrgan = organRes.findByIdAndOrgi(organ.getId(), orgi);
        if (tempOrgan != null) {
            tempOrgan.setName(organ.getName());
            tempOrgan.setUpdatetime(new Date());
            tempOrgan.setOrgi(orgi);
            tempOrgan.setSkill(organ.isSkill());
            tempOrgan.setParent(organ.getParent());
            tempOrgan.setArea(organ.getArea());
            organRes.save(tempOrgan);
            OnlineUserProxy.clean(orgi);
        } else {
            msg = "admin_organ_update_not_exist";
        }

        return msg;
    }
}
