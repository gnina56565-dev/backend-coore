@import ru.mentee.power.crm.model.Lead
@import org.springframework.validation.BindingResult
@import ru.mentee.power.crm.model.LeadStatus

@param Lead lead
@param BindingResult errors = null

<div class="mb-4">
  <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
  <input type="email"
         id="email"
         name="email"
         value="${lead.getEmail()}"
         class="w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors != null && errors.hasFieldErrors("email") ? "border-red-500" : "border-gray-300"}">
  @if(errors != null && errors.hasFieldErrors("email"))
    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("email").getDefaultMessage()}</p>
  @endif
</div>

<div class="mb-4">
  <label for="companyName" class="block text-sm font-medium text-gray-700 mb-1">Компания</label>
  <input type="text"
         id="companyName"
         name="companyName"
         value="${lead.getCompany() != null ? lead.getCompany().getName() : (lead.getCompanyName() != null ? lead.getCompanyName() : "")}"
         class="w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors != null && errors.hasFieldErrors("companyName") ? "border-red-500" : "border-gray-300"}">
  @if(errors != null && errors.hasFieldErrors("companyName"))
    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("companyName").getDefaultMessage()}</p>
  @endif
</div>

<div class="mb-4">
  <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Статус</label>
  <select id="status"
          name="status"
          class="w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors != null && errors.hasFieldErrors("status") ? "border-red-500" : "border-gray-300"}">
    <option value="">Выберите статус</option>
    @for(LeadStatus s : LeadStatus.values())
      <option value="${s.name()}" selected="${lead.getStatus() != null && lead.getStatus().name() == s.name()}">${s}</option>
    @endfor
  </select>
  @if(errors != null && errors.hasFieldErrors("status"))
    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("status").getDefaultMessage()}</p>
  @endif
</div>