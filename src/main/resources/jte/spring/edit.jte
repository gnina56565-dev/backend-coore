@import ru.mentee.power.crm.model.Lead
@import org.springframework.validation.BindingResult
@import ru.mentee.power.crm.model.LeadStatus
@import java.util.UUID

@param Lead lead
@param BindingResult errors = null

@template.layout.main(
  content = @`
    <div class="max-w-md mx-auto mt-8">
      <h1 class="text-2xl font-bold mb-6">Редактировать лида</h1>

      @if(errors != null && errors.hasErrors())
        <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          Исправьте ошибки в форме!
        </div>
      @endif

      @if(errors != null && errors.hasGlobalErrors())
         <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            @for(org.springframework.validation.ObjectError error : errors.getGlobalErrors())
               <p>${error.getDefaultMessage()}</p>
            @endfor
         </div>
      @endif

      <form method="post" action="/leads/${lead.getId().toString()}" class="space-y-4" novalidate>

        <!-- Email -->
        <div class="mb-4">
          <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
          <input type="email"
                 id="email"
                 name="email"
                 value="${lead.getEmail()}"
                 class='w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors != null && errors.hasFieldErrors("email") ? "border-red-500" : "border-gray-300"}'
                 placeholder="example@company.com" />
          @if(errors != null && errors.hasFieldErrors("email"))
            <p class="text-red-600 text-sm mt-1">${errors.getFieldError("email").getDefaultMessage()}</p>
          @endif
        </div>

        <!-- Company -->
        <div class="mb-4">
          <label for="companyName" class="block text-sm font-medium text-gray-700 mb-1">Компания</label>
          <input type="text"
                 id="companyName"
                 name="companyName"
                 value="${lead.getCompany() != null ? lead.getCompany().getName() : (lead.getCompanyName() != null ? lead.getCompanyName() : "")}"
                 class='w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors != null && errors.hasFieldErrors("companyName") ? "border-red-500" : "border-gray-300"}'
                 placeholder="Company A" />
          @if(errors != null && errors.hasFieldErrors("companyName"))
            <p class="text-red-600 text-sm mt-1">${errors.getFieldError("companyName").getDefaultMessage()}</p>
          @endif
        </div>

        <!-- Status -->
        <div class="mb-4">
          <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Статус</label>
          <select id="status" name="status"
                  class='w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors != null && errors.hasFieldErrors("status") ? "border-red-500" : "border-gray-300"}'>
            <option value="">Выберите статус</option>
            @for(LeadStatus status : LeadStatus.values())
              <option
                value="${status.name()}"
                selected="${lead.getStatus() != null && lead.getStatus().name() == status.name()}"
              >${status}</option>
            @endfor
          </select>
          @if(errors != null && errors.hasFieldErrors("status"))
            <p class="text-red-600 text-sm mt-1">${errors.getFieldError("status").getDefaultMessage()}</p>
          @endif
        </div>

        <button type="submit"
                class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500">
          Сохранить
        </button>
        <a href="/leads" class="block text-center text-gray-600 hover:text-gray-800 mt-2">Отмена</a>
      </form>
    </div>
  `
)