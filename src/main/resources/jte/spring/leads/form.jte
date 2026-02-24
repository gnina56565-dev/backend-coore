
<div class="mb-4">
  <label for="email">Email</label>
  <input type="email" name="email" value="${lead.getEmail}"
         class="${errors.hasFieldErrors("email") ? "border-red-500" : "border-gray-300"}">
  @if(errors.hasFieldErrors("email"))
    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("email").defaultMessage}</p>
  @endif
</div>

<div class="mb-4">
  <label for="company">Компания</label>
  <input type="text" name="company" value="${lead.getCompany}"
         class="${errors.hasFieldErrors("company") ? "border-red-500" : "border-gray-300"}">
  @if(errors.hasFieldErrors("company"))
    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("company").defaultMessage}</p>
  @endif
</div>

<div class="mb-4">
  <label for="status">Статус</label>
  @if(errors.hasFieldErrors("status"))
    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("status").defaultMessage}</p>
  @endif
</div>