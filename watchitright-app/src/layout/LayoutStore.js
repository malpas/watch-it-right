import { observable, action } from "mobx";

export class LayoutStore {
  @observable showDropdownContent = false;

  @action
  toggleDropdown() {
    this.showDropdownContent = !this.showDropdownContent;
  }

  @action
  hideDropdown() {
    this.showDropdownContent = false;
  }
}
