-- ================================================================
-- विद्यासेतु AI — बस रूट और स्टॉप्स (Bus Routes & Stops) SQL स्क्रिप्ट
-- ================================================================

CREATE TABLE public.organization_bus_routes (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  parent_organization_id uuid NOT NULL,
  active_session_id uuid NOT NULL,
  bus_id uuid NOT NULL,
  stop_name text NOT NULL,             -- स्टॉप का नाम (उदा. Mansarovar Stop)
  stop_order integer NOT NULL,          -- स्टॉप का क्रम (1, 2, 3...)
  latitude double precision,           -- स्टॉप के स्थिर कोऑर्डिनेट्स
  longitude double precision,          -- स्टॉप के स्थिर कोऑर्डिनेट्स
  scheduled_time time without time zone, -- स्टॉप पर पहुँचने का निर्धारित समय (उदा. '08:15:00')
  is_active boolean NOT NULL DEFAULT true,
  is_deleted boolean NOT NULL DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  created_by uuid,
  updated_by uuid,
  CONSTRAINT organization_bus_routes_pkey PRIMARY KEY (id),
  CONSTRAINT organization_bus_routes_parent_org_fkey FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id) ON DELETE CASCADE,
  CONSTRAINT organization_bus_routes_session_fkey FOREIGN KEY (active_session_id) REFERENCES public.global_sessions(id) ON DELETE CASCADE,
  CONSTRAINT organization_bus_routes_bus_id_fkey FOREIGN KEY (bus_id) REFERENCES public.organization_parent_buses(id) ON DELETE CASCADE,
  CONSTRAINT organization_bus_routes_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id),
  CONSTRAINT organization_bus_routes_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id),
  CONSTRAINT organization_bus_routes_bus_stop_unique UNIQUE (bus_id, stop_name),
  CONSTRAINT organization_bus_routes_bus_order_unique UNIQUE (bus_id, stop_order)
);

-- Row Level Security (RLS) सक्षम करें
ALTER TABLE public.organization_bus_routes ENABLE ROW LEVEL SECURITY;

-- पॉलिसी 1: प्रमाणित उपयोगकर्ताओं (Students/Guardians/Staff) को रूट देखने (SELECT) की अनुमति
CREATE POLICY "Allow read access for authenticated users" 
ON public.organization_bus_routes 
FOR SELECT 
TO authenticated 
USING (is_deleted = false AND is_active = true);

-- पॉलिसी 2: केवल एक्टिव आर्गेनाइजेशन स्टाफ/एडमिन को रूट बदलने (ALL: INSERT, UPDATE, DELETE) की अनुमति
CREATE POLICY "Allow write access for active staff/admin users" 
ON public.organization_bus_routes 
FOR ALL 
TO authenticated 
USING (
  EXISTS (
    SELECT 1 FROM public.organization_users ou
    JOIN public.users u ON u.id = ou.user_id
    WHERE u.auth_id = auth.uid() 
    AND ou.is_active = true 
    AND ou.is_deleted = false
  )
  OR
  EXISTS (
    SELECT 1 FROM public.organization_parents_users opu
    JOIN public.users u ON u.id = opu.user_id
    WHERE u.auth_id = auth.uid() 
    AND opu.is_active = true 
    AND opu.is_deleted = false
  )
)
WITH CHECK (
  EXISTS (
    SELECT 1 FROM public.organization_users ou
    JOIN public.users u ON u.id = ou.user_id
    WHERE u.auth_id = auth.uid() 
    AND ou.is_active = true 
    AND ou.is_deleted = false
  )
  OR
  EXISTS (
    SELECT 1 FROM public.organization_parents_users opu
    JOIN public.users u ON u.id = opu.user_id
    WHERE u.auth_id = auth.uid() 
    AND opu.is_active = true 
    AND opu.is_deleted = false
  )
);

-- रीयल-टाइम रेप्लीकेशन में शामिल करें (Replication/Realtime)
ALTER PUBLICATION supabase_realtime ADD TABLE public.organization_bus_routes;
