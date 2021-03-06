/*
 * Copyright (C) 2012-13 MINHAP, Gobierno de España This program is licensed and may be used,
 * modified and redistributed under the terms of the European Public License (EUPL), either version
 * 1.1 or (at your option) any later version as soon as they are approved by the European
 * Commission. Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * more details. You should have received a copy of the EUPL1.1 license along with this program; if
 * not, you may find it at http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 */

package es.mpt.dsic.inside.ws.service.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Contains a List of {@link Document}
 * 
 * @author Hugo
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "certificateList", propOrder = {"certificates"})
public class CertificateList implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @XmlElement(required = true)
  protected List<byte[]> certificates;

  /**
   * Gets the list of certificates.
   * 
   * @return {@link byte[]} list.
   */
  public List<byte[]> getCertificates() {
    if (certificates == null) {
      certificates = new ArrayList<byte[]>();
    }
    return this.certificates;
  }

}
