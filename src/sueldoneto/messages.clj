(ns sueldoneto.messages)
(def messages
  {:annual-gross          {:pre   "Sueldo bruto anual:"
                           :error "Accepta solo -> Euros en números"}
   :installments          {:pre   "Número de pagas:"
                           :error "Accepta solo -> 12 o 14"}
   :age                   {:pre   "Edad:"
                           :error "Accepta solo -> Años en números"}
   :personal-situation    {:pre   ["Situación familiar:"
                                   "[A] Soltero, viudo, divorciado o separado con hijos a cargo"
                                   "[B] Casado y cuyo cónyuge no obtiene rentas superiores a 1.500 euros anuales"
                                   "[C] Otros"]
                           :error "Accepta solo -> \"A\" \"B\" o \"C\""}
   :contract              {:pre   ["Tipo de contrato laboral:"
                                   "[G] general [I] Duración inferior a doce meses"]
                           :error "Accepta solo \"G\" o \"I\""}
   :professional-category {:pre   ["Categoría profesional:"
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
   :children              {:pre   "Número de hijos menores de 25 años:"
                           :error "Accepta solo -> Cantidad en números"}
   :young-children        {:pre   "De sus hijos, cuántos tienen menos de 3 años?"
                           :error "Accepta solo -> Cantidad en números"}})

