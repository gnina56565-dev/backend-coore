@import ru.mentee.power.crm.model.Lead
@param Lead lead

@template.layout.main(content = @`
<div class="container mx-auto p-6 max-w-lg">
    <div class="bg-white shadow-md rounded-lg p-6">
        <h1 class="text-2xl font-bold mb-4 text-gray-800">Создание сделки из лида</h1>

        <div class="mb-6 bg-gray-50 p-4 rounded border border-gray-200">
            <h3 class="font-semibold text-gray-700 mb-2">Информация о лиде:</h3>
            <p class="text-gray-600"><strong>Email:</strong> ${lead.getEmail()}</p>
            <p class="text-gray-600"><strong>Компания:</strong> ${lead.getCompany() != null ? lead.getCompany().getName() : "Не указана"}</p>
            <p class="text-gray-600"><strong>Текущий статус:</strong> ${lead.getStatus()}</p>
        </div>

        <form action="/deals/convert" method="post" class="space-y-4">
            <input type="hidden" name="leadId" value="${lead.getId().toString()}" />

            <div>
                <label for="amount" class="block text-sm font-medium text-pink-700 mb-1">
                    Сумма сделки (₽)
                </label>
                <input type="number"
                       id="amount"
                       name="amount"
                       step="0.01"
                       min="0"
                       required
                       placeholder="Например: 150000"
                       class="w-full border border-pink-300 rounded-md p-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition">
            </div>

            <div class="flex items-center justify-end space-x-3 pt-4">
                <a href="/leads" class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition">
                    Отмена
                </a>
                <button type="submit" class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition font-semibold">
                    Создать сделку
                </button>
            </div>
        </form>
    </div>
</div>
`)