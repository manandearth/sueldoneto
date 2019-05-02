(ns sueldoneto.messages)
(def messages
  {:annual-gross                 {:pre   "Sueldo bruto anual:"
                                  :error "Accepta solo -> Euros en números"}
   :installments                 {:pre   "Número de pagas:"
                                  :error "Accepta solo -> 12 o 14"}
   :age                          {:pre   "Edad:"
                                  :error "Accepta solo -> Años en números"}
   :personal-situation           {:pre   ["Situación familiar:"
                                          "[A] Soltero, viudo, divorciado o separado con hijos a cargo"
                                          "[B] Casado y cuyo cónyuge no obtiene rentas superiores a 1.500 euros anuales"
                                          "[C] Otros"]
                                  :error "Accepta solo -> \"A\" \"B\" o \"C\""}
   :contract                     {:pre   ["Tipo de contrato laboral:"
                                          "[G] general [I] Duración inferior a doce meses"]
                                  :error "Accepta solo \"G\" o \"I\""}
   :professional-category        {:pre   ["Categoría profesional:"
                                          "[A] Ingenieros y Licenciados"
                                          "[B] Ingenieros Técnicos, Peritos y Ayudantes Titulados"
                                          "[C] Jefes Administrativos y de Taller"
                                          "[D] Ayudantes no Titulados"
                                          "[E] Oficiales Administrativos"
                                          "[F] Subalternos"
                                          "[G] Auxiliares Administrativos"
                                          "[H] Oficiales de primera y segunda"
                                          "[I] Oficiales de tercera y Especialistas"
                                          "[J] Peones"
                                          "[K] Trabajadores menores de dieciocho años, cualquiera"]
                                  :error "Accepta solo -> \"A\" \"B\" \"C\" \"D\" \"E\" \"F\" \"G\" \"H\" \"I\" \"J\" o \"K\""}
   :children                     {:pre   "Número de hijos menores de 25 años:"
                                  :error "Accepta solo -> Cantidad en números"}
   :young-children               {:pre       "De sus hijos, cuántos tienen menos de 3 años?"
                                  :error     "Accepta solo -> Cantidad en números"
                                  :alt-error "Accepta catindad igual o inferior al campo anterior"}
   :exclusivity                  {:pre   ["Tiene los hijos en exclusiva a efectos fiscales?" "[S] si" "[N] no"]
                                  :error "Accepta solo -> \"s\" o \"n\""}
   :ancestors                    {:pre   "Mayores de 65 años y menores de 75 años a cargo"
                                  :error "Accepta solo -> Cantidad en números"}
   :old-ancestors                {:pre   "Número de ascendientes mayores de 75 años a cargo"
                                  :error "Accepta solo -> Cantidad en números"}
   :disabled-dependents          {:pre   "Menor de 65 años a cargo con discapacidad"
                                  :error "Accepta solo -> Cantidad en números"}
   :receiving-benefits           {:pre   "Contribuyentes que aplican los mínimos por ascendiente"
                                  :error "Accepta solo -> Cantidad en números"}
   :m-grade-disabled-descendants {:pre       "Número de descendientes con grado de discapacidad entre el 33% y el 65%"
                                  :error     "Accepta solo -> Cantidad en números"
                                  :alt-error "Accepta cantindad igual o inferior al cantidad de niños con menos de 25 años"}
   :m-grade-disabled-ancestors   {:pre       "Número de ascendientes con grado de discapacidad entre el 33% y el 65%"
                                  :error     "Accepta solo -> Cantidad en números"
                                  :alt-error "Accepta cantindad igual o inferior al cantidad de ascendientes mayores que 65 años a cargo"}
   :h-grade-disabled-descendants {:pre       "Número de descendientes con grado de discapacidad igual o superior al 65%"
                                  :error     "Accepta solo -> Cantidad en números"
                                  :alt-error "Accepta cantindad igual o inferior al cantidad de niños con menos de 25 años menos los con grado de discapacidad entre el 33% y el 65%"}
   :h-grade-disabled-ancestors   {:pre       "Número de ascendientes con grado de discapacidad igual o superior al 65%"
                                  :error     "Accepta solo -> Cantidad en números"
                                  :alt-error "Accepta cantidad igual o inferior al cantidad de ascendientes mayores que 65 años a cargo menos los con grado de discapacidad entre el 33% y el 65%"}})

