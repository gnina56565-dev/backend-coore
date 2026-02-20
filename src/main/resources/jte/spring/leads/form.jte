@param ru.mentee.power.crm.model.Lead lead
@param org.springframework.validation.BindingResult errors
@import ru.mentee.power.crm.model.LeadStatus
<div class="mb-4">
  <label for="email">Email</label>
  <input type="email" name="email" value="${lead.email}"
         class="${errors.hasFieldErrors("email") ? "border-red-500" : "border-gray-300"}">
  @if(errors.hasFieldErrors("email"))
    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("email").defaultMessage}</p>
  @endif
</div>

<div class="mb-4">
  <label for="company">Компания</label>
  <input type="text" name="company" value="${lead.company}"
         class="${errors.hasFieldErrors("company") ? "border-red-500" : "border-gray-300"}">
  @if(errors.hasFieldErrors("company"))
    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("company").defaultMessage}</p>
  @endif
</div>

<div class="mb-4">
  <label for="status">Статус</label>
  <select name="status"
          class="${errors.hasFieldErrors("status") ? "border-red-500" : "border-gray-300"}">
    <option value="">-- Выберите --</option>
    @for(ru.mentee.power.crm.model.LeadStatus s : T(ru.mentee.power.crm.model.LeadStatus).values())
      <option value="${s}" selected="${lead != null && lead.status == s ? "selected" : null}">${s}</option>
    @endfor
  </select>
  @if(errors.hasFieldErrors("status"))
    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("status").defaultMessage}</p>
  @endif
</div>