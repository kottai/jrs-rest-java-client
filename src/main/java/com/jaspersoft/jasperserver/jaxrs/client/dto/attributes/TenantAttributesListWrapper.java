package com.jaspersoft.jasperserver.jaxrs.client.dto.attributes;

import com.jaspersoft.jasperserver.dto.authority.ClientUserAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Alex Krasnyanskiy
 * @since 6.0.1
 */
@Deprecated
@XmlRootElement(name = "attributes")
@SuppressWarnings("deprecation")
public class TenantAttributesListWrapper {

    private List<ClientUserAttribute> attributes;

    public TenantAttributesListWrapper() {
    }

    public TenantAttributesListWrapper(List<ClientUserAttribute> attributes) {
        this.attributes = attributes;
    }

    @XmlElement(name = "attribute")
    public List<ClientUserAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ClientUserAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantAttributesListWrapper that = (TenantAttributesListWrapper) o;
        return !(attributes != null
                ? !attributes.equals(that.attributes)
                : that.attributes != null);
    }

    @Override
    public int hashCode() {
        return attributes != null ? attributes.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TenantAttributesListWrapper{" +
                "attributes=" + attributes +
                '}';
    }
}
