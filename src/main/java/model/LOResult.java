package model;

import java.util.List;

public class LOResult {
  int id;
  private List<VLMembers> vlm;
  private List<HLMembers> hlm;

  public LOResult() {
  }

  public LOResult(List<VLMembers> vl, List<HLMembers> hl) {
    hlm = hl;
    vlm = vl;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public List<HLMembers> getHLMember() {
    return hlm;
  }

  public void setHLMember(List<HLMembers> hl) {
    this.hlm = hl;
  }

  public List<VLMembers> getVLMember() {
    return vlm;
  }

  public void setVLMember(List<VLMembers> vl) {
    this.vlm = vl;
  }

}
