-- Supabase SQL Editor में रन करने के लिए स्क्रिप्ट
-- यह फ़ंक्शन वर्तमान वर्क्सपेस के अनुसार सभी 14 टेबल्स का डेटा एक ही JSON पेलोड में एकीकृत (consolidate) करके लौटाता है।
-- इसमें 100-आर्गुमेंट सीमा, टेबल नेम अलाइनमेंट (जैसे public.organization_bus_routes), और नल/खाली आईडी वाली सभी समस्याओं का समाधान शामिल है।

CREATE OR REPLACE FUNCTION public.fetch_entire_workspace_payload_for_mobile(
    p_parent_organization_id UUID,
    p_child_organization_id UUID, -- स्टाफ के लिए कोटलिन से NULL (JsonNull) आएगा
    p_user_role TEXT,             -- 'Student', 'Guardian', 'DRIVER', 'Teacher', आदि
    p_user_id UUID,               -- Auth User ID (public.users.id)
    p_last_synced_at TIMESTAMPTZ DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_student_id UUID;
    v_guardian_id UUID;
    v_staff_id UUID;
    v_bus_id UUID;
    v_resolved_parent_org_id UUID := p_parent_organization_id;
    v_response JSONB;
BEGIN
    -- ==========================================
    -- 1. रोल के अनुसार एसोसिएटेड आईडी रिज़ॉल्व करें
    -- ==========================================
    
    -- क) STUDENT रोल के लिए:
    IF p_user_role = 'Student' THEN
        SELECT student_id INTO v_student_id 
        FROM public.organization_student_user_links 
        WHERE user_id = p_user_id AND organization_id = p_child_organization_id AND is_approved = true LIMIT 1;
        
    -- ख) GUARDIAN रोल के लिए:
    ELSIF p_user_role = 'Guardian' THEN
        SELECT guardian_id INTO v_guardian_id 
        FROM public.organization_guardian_user_links 
        WHERE user_id = p_user_id AND organization_id = p_child_organization_id AND is_approved = true LIMIT 1;
        
    -- ग) DRIVER रोल के लिए:
    ELSIF p_user_role = 'DRIVER' THEN
        SELECT staff_id, parent_organization_id INTO v_staff_id, v_resolved_parent_org_id 
        FROM public.organization_parent_staff_user_links 
        WHERE user_id = p_user_id LIMIT 1;
        
        SELECT bus_id INTO v_bus_id 
        FROM public.organization_parent_bus_staff_assignments 
        WHERE staff_id = v_staff_id AND is_active = true AND is_deleted = false AND role_in_bus = 'Driver' LIMIT 1;
        
    -- घ) अन्य सभी STAFF रोल्स के लिए:
    ELSE
        SELECT staff_id, parent_organization_id INTO v_staff_id, v_resolved_parent_org_id 
        FROM public.organization_parent_staff_user_links 
        WHERE user_id = p_user_id LIMIT 1;
    END IF;

    -- ==========================================
    -- 2. डेटा को एग्रीगेट और कम्बाइन करें (JSONB Object)
    -- ==========================================
    SELECT jsonb_build_object(
        -- ----------------------------------------------------
        -- ENTITY 1: Child Org Setup (चाइल्ड सेटअप विवरण)
        -- ----------------------------------------------------
        'setups', (
            SELECT COALESCE(jsonb_agg(jsonb_build_object(
                'organization_id', org.id,
                'organization_name', org.name,
                'email', prof.email,
                'mobile_number', prof.mobile_number,
                'alternate_mobile_number', prof.alternate_mobile_number,
                'address_line1', prof.address_line1,
                'address_line2', prof.address_line2,
                'city', prof.city,
                'state', prof.state,
                'pincode', prof.pincode,
                'session_id', prof.active_session_id,
                'session_name', sess.name,
                'is_setup_complete', COALESCE(prof.is_setup_complete, false),
                'boards_json', COALESCE((
                    SELECT jsonb_agg(jsonb_build_object('id', gb.id, 'name', gb.name))
                    FROM public.organization_boards ob
                    JOIN public.global_boards gb ON ob.board_id = gb.id
                    WHERE ob.organization_id = org.id AND ob.is_active = true AND ob.is_deleted = false
                ), '[]'::jsonb)::text,
                'mediums_json', COALESCE((
                    SELECT jsonb_agg(jsonb_build_object('id', gm.id, 'name', gm.name))
                    FROM public.organization_mediums om
                    JOIN public.global_mediums gm ON om.medium_id = gm.id
                    WHERE om.organization_id = org.id AND om.is_active = true AND om.is_deleted = false
                ), '[]'::jsonb)::text,
                'languages_json', COALESCE((
                    SELECT jsonb_agg(jsonb_build_object('id', gl.id, 'name', gl.name))
                    FROM public.organization_languages ol
                    JOIN public.global_languages gl ON ol.language_id = gl.id
                    WHERE ol.organization_id = org.id AND ol.is_active = true AND ol.is_deleted = false
                ), '[]'::jsonb)::text,
                'periods_json', COALESCE((
                    SELECT jsonb_agg(jsonb_build_object(
                        'id', op.id, 
                        'name', op.name, 
                        'start_time', op.start_time, 
                        'end_time', op.end_time, 
                        'is_break', COALESCE(op.is_break, false)
                    ))
                    FROM public.organization_periods op
                    WHERE op.organization_id = org.id AND op.is_active = true
                ), '[]'::jsonb)::text,
                'fees_structure_json', COALESCE((
                    SELECT jsonb_agg(jsonb_build_object(
                        'id', fa.id,
                        'organization_class_id', fa.organization_class_id,
                        'fee_head_id', fa.fee_head_id,
                        'amount', fa.amount
                    ))
                    FROM public.organization_fee_assignments fa
                    WHERE fa.organization_id = org.id AND fa.is_active = true AND fa.is_deleted = false
                ), '[]'::jsonb)::text,
                'class_structure_json', COALESCE((
                    SELECT jsonb_agg(jsonb_build_object(
                        'class_id', oc.class_id,
                        'class_name', COALESCE(oc.custom_class_name, gc.name),
                        'sections', (
                            SELECT COALESCE(jsonb_agg(jsonb_build_object(
                                'section_id', sec.id,
                                'section_name', sec.name,
                                'max_capacity', COALESCE(sec.max_capacity, 40),
                                'class_teacher_id', COALESCE(oss.class_teacher_id::text, ''),
                                'class_teacher_name', COALESCE(staff.name, '')
                            )), '[]'::jsonb)
                            FROM public.organization_sections sec
                            LEFT JOIN public.organization_session_classes osclass 
                              ON osclass.organization_class_id = oc.id 
                              AND osclass.organization_id = org.id 
                              AND osclass.session_id = prof.active_session_id 
                              AND osclass.is_active = true 
                              AND osclass.is_deleted = false
                            LEFT JOIN public.organization_session_sections oss 
                              ON oss.organization_session_class_id = osclass.id 
                              AND oss.organization_section_id = sec.id 
                              AND oss.is_active = true 
                              AND oss.is_deleted = false
                            LEFT JOIN public.organization_parent_staff staff 
                              ON oss.class_teacher_id = staff.id
                            WHERE sec.organization_class_id = oc.id 
                              AND sec.is_active = true 
                              AND sec.is_deleted = false
                        ),
                        'subjects', (
                            SELECT COALESCE(jsonb_agg(jsonb_build_object(
                                'subject_id', osub.subject_id,
                                'subject_name', gsub.name,
                                'subject_code', COALESCE(gsub.code, ''),
                                'subject_teacher_id', COALESCE(osub.subject_teacher_id::text, ''),
                                'subject_teacher_name', COALESCE(staff.name, ''),
                                'is_elective', COALESCE(osub.is_elective, false)
                            )), '[]'::jsonb)
                            FROM public.organization_session_classes osclass
                            JOIN public.organization_session_subjects osub 
                              ON osub.organization_session_class_id = osclass.id 
                              AND osub.is_active = true 
                              AND osub.is_deleted = false
                            JOIN public.global_subjects gsub 
                              ON osub.subject_id = gsub.id
                            LEFT JOIN public.organization_parent_staff staff 
                              ON osub.subject_teacher_id = staff.id
                            WHERE osclass.organization_class_id = oc.id 
                              AND osclass.organization_id = org.id 
                              AND osclass.session_id = prof.active_session_id 
                              AND osclass.is_active = true 
                              AND osub.is_deleted = false
                        )
                    ))
                    FROM public.organization_classes oc
                    LEFT JOIN public.global_classes gc ON oc.class_id = gc.id
                    WHERE oc.organization_id = org.id AND oc.is_active = true AND oc.is_deleted = false
                ), '[]'::jsonb)::text
            )), '[]'::jsonb)
            FROM public.organizations org
            LEFT JOIN public.organization_profiles prof ON prof.organization_id = org.id
            LEFT JOIN public.global_sessions sess ON prof.active_session_id = sess.id
            WHERE (
                (p_user_role IN ('Student', 'Guardian') AND org.id = p_child_organization_id)
                OR
                (p_user_role NOT IN ('Student', 'Guardian') AND org.parent_organization_id = v_resolved_parent_org_id)
            )
            AND org.is_active = true AND org.is_deleted = false
        ),

        -- ----------------------------------------------------
        -- ENTITY 2: Students (छात्रों की विस्तृत सूची - 100 आर्गुमेंट फिक्स के साथ)
        -- ----------------------------------------------------
        'students', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', s.id,
                    'organization_id', s.organization_id,
                    'active_session_id', s.active_session_id,
                    'name', s.name,
                    'gender', s.gender,
                    'sr_number', s.sr_number,
                    'admission_date', s.admission_date,
                    'date_of_birth', s.date_of_birth,
                    'enrollment_number', s.enrollment_number,
                    'image_url', s.image_url,
                    'is_active', s.is_active,
                    'is_deleted', s.is_deleted,
                    'guardian_id', s.guardian_id,
                    'guardian_name', g.name,
                    'guardian_mobile', g.mobile_number,
                    'guardian_relationship_name', grt.name,
                    'category_id', s.category_id,
                    'category_name', gsc.name,
                    'blood_group_id', s.blood_group_id,
                    'blood_group_name', gbg.name,
                    'student_status_id', s.student_status_id,
                    'student_status_name', gss.name,
                    'address_area_id', s.address_area_id,
                    'address_area_name', ga1.name,
                    'address_details', s.address_details,
                    'class_id', e.class_id,
                    'class_name', COALESCE(oc.custom_class_name, gc.name),
                    'section_id', e.section_id,
                    'section_name', os.name,
                    'roll_number', e.roll_number
                )
                ||
                jsonb_build_object(
                    'qr_identity_id', q.id,
                    'qr_token_hash', q.qr_token_hash,
                    'qr_status', q.status,
                    'qr_expiry_date', q.expiry_date,
                    'id_card_id', c.id,
                    'card_number', c.card_number,
                    'id_card_status', c.status,
                    'id_card_reissue_reason', c.reason_for_reissue,
                    'home_latitude', l.latitude,
                    'home_longitude', l.longitude,
                    'mother_tongue_id', d.mother_tongue_id,
                    'mother_tongue_name', gl.name,
                    'religion', d.religion,
                    'nationality', COALESCE(d.nationality, 'Indian'),
                    'identification_mark', d.identification_mark,
                    'is_single_girl_child', COALESCE(d.is_single_girl_child, false),
                    'caste_certificate_number', d.caste_certificate_number,
                    'father_name', d.father_name,
                    'father_mobile', d.father_mobile,
                    'father_email', d.father_email,
                    'father_qualification', d.father_qualification,
                    'father_occupation', d.father_occupation,
                    'parents_aadhaar_father', d.parents_aadhaar_father,
                    'mother_name', d.mother_name,
                    'mother_mobile', d.mother_mobile,
                    'mother_email', d.mother_email,
                    'mother_qualification', d.mother_qualification,
                    'mother_occupation', d.mother_occupation,
                    'parents_aadhaar_mother', d.parents_aadhaar_mother,
                    'family_annual_income', COALESCE(d.family_annual_income, 0.0)
                )
                ||
                jsonb_build_object(
                    'permanent_address_details', d.permanent_address_details,
                    'permanent_address_area', d.permanent_address_area,
                    'permanent_address_area_id', d.permanent_address_area_id,
                    'permanent_area_name', ga2.name,
                    'previous_school_name', d.previous_school_name,
                    'previous_class', d.previous_class,
                    'previous_board', d.previous_board,
                    'tc_number', d.tc_number,
                    'tc_date', d.tc_date,
                    'previous_marks', d.previous_marks,
                    'height', d.height,
                    'weight', d.weight,
                    'medical_conditions', d.medical_conditions,
                    'regular_medications', d.regular_medications,
                    'emergency_contact_name', d.emergency_contact_name,
                    'emergency_contact_phone', d.emergency_contact_phone,
                    'bank_account_number', d.bank_account_number,
                    'bank_name', d.bank_name,
                    'bank_branch', d.bank_branch,
                    'bank_ifsc', d.bank_ifsc,
                    'bank_account_holder', d.bank_account_holder,
                    'student_aadhar', d.student_aadhar
                )
            ), '[]'::jsonb)
            FROM public.organization_students s
            LEFT JOIN public.organization_student_enrollments e ON e.student_id = s.id AND e.is_active = true AND e.is_deleted = false
            LEFT JOIN public.organization_student_additional_details d ON d.student_id = s.id
            LEFT JOIN public.organization_guardians g ON s.guardian_id = g.id
            LEFT JOIN public.organization_student_qr_identities q ON q.student_id = s.id AND q.is_active = true AND q.is_deleted = false AND q.status = 'Active'
            LEFT JOIN public.organization_student_id_cards c ON c.student_id = s.id AND c.is_active = true AND c.is_deleted = false AND c.status = 'Active'
            LEFT JOIN public.organization_student_home_locations l ON l.student_id = s.id
            LEFT JOIN public.organization_student_bus_assignments ba ON ba.student_id = s.id AND ba.is_active = true AND ba.is_deleted = false
            LEFT JOIN public.global_relationship_types grt ON g.relationship_type_id = grt.id
            LEFT JOIN public.global_student_categories gsc ON s.category_id = gsc.id
            LEFT JOIN public.global_blood_groups gbg ON s.blood_group_id = gbg.id
            LEFT JOIN public.global_student_status gss ON s.student_status_id = gss.id
            LEFT JOIN public.global_areas ga1 ON s.address_area_id = ga1.id
            LEFT JOIN public.global_areas ga2 ON d.permanent_address_area_id = ga2.id
            LEFT JOIN public.global_languages gl ON d.mother_tongue_id = gl.id
            LEFT JOIN public.organization_classes oc ON e.class_id = oc.id
            LEFT JOIN public.global_classes gc ON oc.class_id = gc.id
            LEFT JOIN public.organization_sections os ON e.section_id = os.id
            WHERE (
                (p_user_role IN ('Student', 'Guardian') AND s.organization_id = p_child_organization_id)
                OR
                (p_user_role NOT IN ('Student', 'Guardian') AND s.organization_id IN (
                    SELECT orgs.id FROM public.organizations orgs WHERE orgs.parent_organization_id = v_resolved_parent_org_id
                ))
            )
            AND (
                (p_user_role = 'Student' AND s.id = v_student_id) 
                OR
                (p_user_role = 'Guardian' AND s.guardian_id = v_guardian_id) 
                OR
                (p_user_role = 'DRIVER' AND ba.bus_id = v_bus_id) 
                OR
                (p_user_role NOT IN ('Student', 'Guardian', 'DRIVER'))
            )
            AND (
                p_last_synced_at IS NULL OR
                s.updated_at > p_last_synced_at OR
                e.updated_at > p_last_synced_at OR
                d.updated_at > p_last_synced_at OR
                g.updated_at > p_last_synced_at
            )
        ),

        -- ----------------------------------------------------
        -- ENTITY 3: Student Additional Fees (छात्रों के अतिरिक्त शुल्क)
        -- ----------------------------------------------------
        'student_additional_fees', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', f.id,
                    'organization_id', f.organization_id,
                    'active_session_id', f.active_session_id,
                    'student_id', f.student_id,
                    'global_fee_head_id', f.global_fee_head_id,
                    'global_fee_head_name', h.name,
                    'global_fee_head_code', h.code,
                    'amount', f.amount,
                    'is_active', f.is_active,
                    'is_deleted', f.is_deleted
                )
            ), '[]'::jsonb)
            FROM public.organization_student_additional_fees f
            LEFT JOIN public.global_fee_heads h ON f.global_fee_head_id = h.id
            WHERE (
                (p_user_role IN ('Student', 'Guardian') AND f.organization_id = p_child_organization_id)
                OR
                (p_user_role NOT IN ('Student', 'Guardian') AND f.organization_id IN (
                    SELECT orgs.id FROM public.organizations orgs WHERE orgs.parent_organization_id = v_resolved_parent_org_id
                ))
            )
            AND (
                (p_user_role = 'Student' AND f.student_id = v_student_id)
                OR
                (p_user_role = 'Guardian' AND f.student_id IN (
                    SELECT id FROM public.organization_students WHERE guardian_id = v_guardian_id
                ))
                OR
                (p_user_role NOT IN ('Student', 'Guardian'))
            )
            AND (p_last_synced_at IS NULL OR f.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 4: Student Fee Payments (शुल्क भुगतान इतिहास)
        -- ----------------------------------------------------
        'student_fee_payments', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', p.id,
                    'organization_id', p.organization_id,
                    'active_session_id', p.active_session_id,
                    'student_id', p.student_id,
                    'receipt_number', p.receipt_number,
                    'payment_mode', p.payment_mode,
                    'payment_date', p.payment_date,
                    'amount_paid', p.amount_paid,
                    'discount_amount', p.discount_amount,
                    'fine_amount', p.fine_amount,
                    'discount_reason', p.discount_reason,
                    'cash_received_by_user_id', p.cash_received_by_user_id,
                    'cash_received_by_user_name', u.email,
                    'cheque_number', p.cheque_number,
                    'cheque_date', p.cheque_date,
                    'cheque_bank_name', p.cheque_bank_name,
                    'online_transaction_id', p.online_transaction_id,
                    'online_payment_app', p.online_payment_app,
                    'remarks', p.remarks,
                    'status', p.status,
                    'is_active', p.is_active,
                    'is_deleted', p.is_deleted
                )
            ), '[]'::jsonb)
            FROM public.organization_student_fee_payments p
            LEFT JOIN public.users u ON p.cash_received_by_user_id = u.id
            WHERE (
                (p_user_role IN ('Student', 'Guardian') AND p.organization_id = p_child_organization_id)
                OR
                (p_user_role NOT IN ('Student', 'Guardian') AND p.organization_id IN (
                    SELECT orgs.id FROM public.organizations orgs WHERE orgs.parent_organization_id = v_resolved_parent_org_id
                ))
            )
            AND (
                (p_user_role = 'Student' AND p.student_id = v_student_id)
                OR
                (p_user_role = 'Guardian' AND p.student_id IN (
                    SELECT id FROM public.organization_students WHERE guardian_id = v_guardian_id
                ))
                OR
                (p_user_role NOT IN ('Student', 'Guardian'))
            )
            AND (p_last_synced_at IS NULL OR p.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 5: Student Attendance (छात्र उपस्थिति रिकॉर्ड)
        -- ----------------------------------------------------
        'student_attendance', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', a.id,
                    'organization_id', a.organization_id,
                    'active_session_id', a.active_session_id,
                    'student_id', a.student_id,
                    'student_name', s.name,
                    'roll_number', e.roll_number,
                    'class_id', e.class_id,
                    'class_name', gc.name,
                    'section_id', e.section_id,
                    'section_name', os.name,
                    'attendance_date', a.attendance_date,
                    'status', a.status,
                    'remarks', a.remarks,
                    'marked_by_staff_id', a.marked_by_staff_id,
                    'marked_by_staff_name', st.name,
                    'is_active', a.is_active,
                    'is_deleted', a.is_deleted
                )
            ), '[]'::jsonb)
            FROM public.organization_student_attendance a
            LEFT JOIN public.organization_students s ON a.student_id = s.id
            LEFT JOIN public.organization_student_enrollments e ON e.student_id = s.id AND e.is_active = true AND e.is_deleted = false
            LEFT JOIN public.organization_classes oc ON e.class_id = oc.id
            LEFT JOIN public.global_classes gc ON oc.class_id = gc.id
            LEFT JOIN public.organization_sections os ON e.section_id = os.id
            LEFT JOIN public.organization_parent_staff st ON a.marked_by_staff_id = st.id
            WHERE (
                (p_user_role IN ('Student', 'Guardian') AND a.organization_id = p_child_organization_id)
                OR
                (p_user_role NOT IN ('Student', 'Guardian') AND a.organization_id IN (
                    SELECT orgs.id FROM public.organizations orgs WHERE orgs.parent_organization_id = v_resolved_parent_org_id
                ))
            )
            AND (
                (p_user_role = 'Student' AND a.student_id = v_student_id)
                OR
                (p_user_role = 'Guardian' AND a.student_id IN (
                    SELECT id FROM public.organization_students WHERE guardian_id = v_guardian_id
                ))
                OR
                (p_user_role NOT IN ('Student', 'Guardian'))
            )
            AND (p_last_synced_at IS NULL OR a.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 6: Buses (बस विवरण)
        -- ----------------------------------------------------
        'buses', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', b.id,
                    'parent_organization_id', b.parent_organization_id,
                    'active_session_id', b.active_session_id,
                    'bus_number', b.bus_number,
                    'bus_name', b.bus_name,
                    'route_name', b.route_name,
                    'max_capacity', b.max_capacity,
                    'is_active', b.is_active,
                    'is_deleted', b.is_deleted,
                    'insurance_expiry_date', b.insurance_expiry_date,
                    'insurance_image_url', b.insurance_image_url,
                    'fitness_expiry_date', b.fitness_expiry_date,
                    'fitness_image_url', b.fitness_image_url,
                    'pollution_expiry_date', b.pollution_expiry_date,
                    'pollution_image_url', b.pollution_image_url,
                    'driver_id', (
                        SELECT a.staff_id 
                        FROM public.organization_parent_bus_staff_assignments a 
                        WHERE a.bus_id = b.id AND a.role_in_bus = 'Driver' AND a.is_active = true AND a.is_deleted = false 
                        LIMIT 1
                    ),
                    'driver_name', (
                        SELECT st.name 
                        FROM public.organization_parent_bus_staff_assignments a 
                        JOIN public.organization_parent_staff st ON a.staff_id = st.id
                        WHERE a.bus_id = b.id AND a.role_in_bus = 'Driver' AND a.is_active = true AND a.is_deleted = false 
                        LIMIT 1
                    ),
                    'driver_mobile', (
                        SELECT st.mobile_number 
                        FROM public.organization_parent_bus_staff_assignments a 
                        JOIN public.organization_parent_staff st ON a.staff_id = st.id
                        WHERE a.bus_id = b.id AND a.role_in_bus = 'Driver' AND a.is_active = true AND a.is_deleted = false 
                        LIMIT 1
                    ),
                    'conductor_id', (
                        SELECT a.staff_id 
                        FROM public.organization_parent_bus_staff_assignments a 
                        WHERE a.bus_id = b.id AND a.role_in_bus = 'Conductor' AND a.is_active = true AND a.is_deleted = false 
                        LIMIT 1
                    ),
                    'conductor_name', (
                        SELECT st.name 
                        FROM public.organization_parent_bus_staff_assignments a 
                        JOIN public.organization_parent_staff st ON a.staff_id = st.id
                        WHERE a.bus_id = b.id AND a.role_in_bus = 'Conductor' AND a.is_active = true AND a.is_deleted = false 
                        LIMIT 1
                    ),
                    'conductor_mobile', (
                        SELECT st.mobile_number 
                        FROM public.organization_parent_bus_staff_assignments a 
                        JOIN public.organization_parent_staff st ON a.staff_id = st.id
                        WHERE a.bus_id = b.id AND a.role_in_bus = 'Conductor' AND a.is_active = true AND a.is_deleted = false 
                        LIMIT 1
                    )
                )
            ), '[]'::jsonb)
            FROM public.organization_parent_buses b
            WHERE b.parent_organization_id = v_resolved_parent_org_id
            AND (
                -- Admin/Transport power roles → parent org ki sabhi buses
                p_user_role IN (
                    'Principal', 'System Administrator', 'School Administrator',
                    'Admin', 'Org Admin', 'Director', 'Owner',
                    'Transport Manager', 'Transport Coordinator',
                    'Accountant', 'Gatekeeper'
                )
                -- Driver → kewal vahi bus jisme vo driver hai
                OR (p_user_role = 'DRIVER' AND b.id = v_bus_id)
                -- Student → kewal apni assigned bus
                OR (p_user_role = 'Student' AND b.id IN (
                    SELECT bus_id FROM public.organization_student_bus_assignments WHERE student_id = v_student_id
                ))
                -- Guardian → kewal unke bachon ki assigned buses
                OR (p_user_role = 'Guardian' AND b.id IN (
                    SELECT sba.bus_id 
                    FROM public.organization_student_bus_assignments sba
                    JOIN public.organization_students s ON sba.student_id = s.id
                    WHERE s.guardian_id = v_guardian_id
                ))
            )
            -- Buses ka data hamesha poora bhejo, koi last_synced_at filter nahi
        ),

        -- ----------------------------------------------------
        -- ENTITY 7: Bus Routes (बस मार्ग और जीपीएस स्टॉप्स)
        -- ----------------------------------------------------
        'bus_routes', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', r.id,
                    'parent_organization_id', r.parent_organization_id,
                    'active_session_id', r.active_session_id,
                    'bus_id', r.bus_id,
                    'stop_name', r.stop_name,
                    'stop_order', r.stop_order,
                    'latitude', r.latitude,
                    'longitude', r.longitude,
                    'scheduled_time', r.scheduled_time,
                    'is_active', r.is_active,
                    'is_deleted', r.is_deleted
                )
            ), '[]'::jsonb)
            FROM public.organization_bus_routes r
            WHERE r.parent_organization_id = v_resolved_parent_org_id
            AND (
                p_user_role NOT IN ('Student', 'Guardian', 'DRIVER')
                OR (p_user_role = 'DRIVER' AND r.bus_id = v_bus_id)
                OR (p_user_role IN ('Student', 'Guardian') AND r.bus_id IN (
                    SELECT bus_id FROM public.organization_student_bus_assignments WHERE student_id = v_student_id
                ))
            )
            AND (p_last_synced_at IS NULL OR r.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 8: Staff Directory (स्टाफ निर्देशिका - प्राइवेसी प्रोटेक्टेड)
        -- ----------------------------------------------------
        'staff_members', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', st.id,
                    'parent_organization_id', st.parent_organization_id,
                    'active_session_id', st.active_session_id,
                    'name', st.name,
                    'mobile_number', st.mobile_number,
                    'email', st.email,
                    'gender', st.gender,
                    'is_active', st.is_active,
                    'is_deleted', st.is_deleted,
                    'date_of_joining', st.date_of_joining,
                    'date_of_birth', st.date_of_birth,
                    'pan_number', st.pan_number,
                    'aadhaar_number', st.aadhaar_number,
                    'license_number', st.license_number,
                    'license_expiry_date', st.license_expiry_date,
                    'role_id', st.role_id,
                    'subject_id', st.subject_id,
                    'address_area_id', st.address_area_id
                )
            ), '[]'::jsonb)
            FROM public.organization_parent_staff st
            WHERE st.parent_organization_id = v_resolved_parent_org_id
            -- प्राइवेसी नियम: केवल स्टाफ मेंबर्स ही पूरी स्टाफ डायरेक्टरी देख सकते हैं
            AND (p_user_role NOT IN ('Student', 'Guardian'))
            AND (p_last_synced_at IS NULL OR st.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 9: Expenses (विद्यालय खर्च विवरण - एडमिन ओनली)
        -- ----------------------------------------------------
        'expenses', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', ex.id,
                    'parent_organization_id', ex.parent_organization_id,
                    'active_session_id', ex.active_session_id,
                    'expense_type_id', ex.expense_type_id,
                    'payment_method', ex.payment_method,
                    'amount', ex.amount,
                    'receipt_uuid', ex.receipt_uuid,
                    'admin_note', ex.admin_note,
                    'expense_date', ex.expense_date,
                    'reference_id', ex.reference_id,
                    'reference_type', ex.reference_type,
                    'vendor_name', ex.vendor_name,
                    'bill_number', ex.bill_number,
                    'is_active', ex.is_active,
                    'created_by', ex.created_by
                )
            ), '[]'::jsonb)
            FROM public.organization_parent_expenses ex
            WHERE ex.parent_organization_id = v_resolved_parent_org_id
            -- सुरक्षा नियम: केवल स्कूल एडमिन/मैनेजमेंट स्टाफ ही खर्चे देख सकते हैं
            AND (p_user_role IN ('System Administrator', 'School Administrator', 'Org Admin', 'Principal'))
            AND (p_last_synced_at IS NULL OR ex.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 10: Exams (परीक्षा अनुसूची)
        -- ----------------------------------------------------
        'exams', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', exam.id,
                    'organization_id', exam.organization_id,
                    'active_session_id', exam.active_session_id,
                    'exam_type_id', exam.exam_type_id,
                    'name', exam.name,
                    'start_date', exam.start_date,
                    'end_date', exam.end_date,
                    'is_active', exam.is_active,
                    'is_deleted', exam.is_deleted
                )
            ), '[]'::jsonb)
            FROM public.organization_exams exam
            WHERE (
                (p_user_role IN ('Student', 'Guardian') AND exam.organization_id = p_child_organization_id)
                OR
                (p_user_role NOT IN ('Student', 'Guardian') AND exam.organization_id IN (
                    SELECT orgs.id FROM public.organizations orgs WHERE orgs.parent_organization_id = v_resolved_parent_org_id
                ))
            )
            AND (p_last_synced_at IS NULL OR exam.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 11: Bus Trips (बस यात्रा शेड्यूल)
        -- ----------------------------------------------------
        'bus_trips', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', t.id,
                    'parent_organization_id', t.parent_organization_id,
                    'active_session_id', t.active_session_id,
                    'bus_id', t.bus_id,
                    'bus_number', b.bus_number,
                    'bus_name', b.bus_name,
                    'driver_id', t.driver_id,
                    'driver_name', st.name,
                    'driver_phone', st.mobile_number,
                    'trip_type', t.trip_type,
                    'status', t.status,
                    'start_time', t.start_time,
                    'end_time', t.end_time,
                    'is_active', t.is_active,
                    'is_deleted', t.is_deleted
                )
            ), '[]'::jsonb)
            FROM public.organization_parent_bus_trips t
            LEFT JOIN public.organization_parent_buses b ON t.bus_id = b.id
            LEFT JOIN public.organization_parent_staff st ON t.driver_id = st.id
            WHERE t.parent_organization_id = v_resolved_parent_org_id
            AND (
                p_user_role NOT IN ('Student', 'Guardian', 'DRIVER')
                OR (p_user_role = 'DRIVER' AND t.driver_id = v_staff_id)
                OR (p_user_role IN ('Student', 'Guardian') AND t.bus_id IN (
                    SELECT bus_id FROM public.organization_student_bus_assignments WHERE student_id = v_student_id
                ))
            )
            AND (p_last_synced_at IS NULL OR t.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 12: Bus Trip Attendance Logs (बोर्डिंग लॉग्स)
        -- ----------------------------------------------------
        'bus_trip_attendance_logs', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', l.id,
                    'parent_organization_id', l.parent_organization_id,
                    'organization_id', l.organization_id,
                    'active_session_id', l.active_session_id,
                    'trip_id', l.trip_id,
                    'student_id', l.student_id,
                    'student_name', s.name,
                    'roll_number', e.roll_number,
                    'class_name', gc.name,
                    'section_name', os.name,
                    'status', l.status,
                    'scan_latitude', l.scan_latitude,
                    'scan_longitude', l.scan_longitude,
                    'scanned_at', l.scanned_at,
                    'scanned_by_staff_id', l.scanned_by_staff_id,
                    'scanned_by_staff_name', st.name,
                    'is_active', l.is_active,
                    'is_deleted', l.is_deleted
                )
            ), '[]'::jsonb)
            FROM public.organization_parent_bus_trip_attendance_logs l
            LEFT JOIN public.organization_students s ON l.student_id = s.id
            LEFT JOIN public.organization_student_enrollments e ON e.student_id = s.id AND e.is_active = true AND e.is_deleted = false
            LEFT JOIN public.organization_classes oc ON e.class_id = oc.id
            LEFT JOIN public.global_classes gc ON oc.class_id = gc.id
            LEFT JOIN public.organization_sections os ON e.section_id = os.id
            LEFT JOIN public.organization_parent_staff st ON l.scanned_by_staff_id = st.id
            WHERE l.parent_organization_id = v_resolved_parent_org_id
            AND (
                p_user_role NOT IN ('Student', 'Guardian', 'DRIVER')
                OR (p_user_role = 'DRIVER' AND l.trip_id IN (
                    SELECT id FROM public.organization_parent_bus_trips WHERE driver_id = v_staff_id
                ))
                OR (p_user_role = 'Student' AND l.student_id = v_student_id)
                OR (p_user_role = 'Guardian' AND l.student_id IN (
                    SELECT id FROM public.organization_students WHERE guardian_id = v_guardian_id
                ))
            )
            AND (p_last_synced_at IS NULL OR l.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 13: Calendar Events (स्कूल कैलेंडर/इवेंट्स)
        -- ----------------------------------------------------
        'calendar_events', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', ev.id,
                    'parent_organization_id', ev.parent_organization_id,
                    'organization_id', ev.organization_id,
                    'active_session_id', ev.active_session_id,
                    'name', ev.name,
                    'description', ev.description,
                    'start_date', ev.start_date,
                    'end_date', ev.end_date,
                    'event_type', ev.event_type,
                    'is_school_closed', ev.is_school_closed,
                    'is_active', ev.is_active,
                    'is_deleted', ev.is_deleted
                )
            ), '[]'::jsonb)
            FROM public.organization_calendar_events ev
            WHERE ev.parent_organization_id = v_resolved_parent_org_id
            AND (p_last_synced_at IS NULL OR ev.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 14: Exam Subject Settings (परीक्षा विषय सेटिंग्स)
        -- ----------------------------------------------------
        'exam_subject_settings', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', s.id,
                    'organization_id', s.organization_id,
                    'active_session_id', s.active_session_id,
                    'exam_id', s.exam_id,
                    'class_id', s.class_id,
                    'subject_id', s.subject_id,
                    'class_name', (
                        SELECT COALESCE(oc.custom_class_name, gc.name)
                        FROM public.organization_classes oc
                        LEFT JOIN public.global_classes gc ON oc.class_id = gc.id
                        WHERE oc.id = s.class_id LIMIT 1
                    ),
                    'subject_name', (
                        SELECT gs.name
                        FROM public.global_subjects gs
                        WHERE gs.id = s.subject_id LIMIT 1
                    ),
                    'max_marks', s.max_marks,
                    'minimum_passing_marks', s.minimum_passing_marks,
                    'grading_system', s.grading_system,
                    'is_deleted', s.is_deleted
                )
            ), '[]'::jsonb)
            FROM public.organization_exam_subject_settings s
            WHERE s.organization_id IN (
                SELECT orgs.id FROM public.organizations orgs WHERE orgs.parent_organization_id = v_resolved_parent_org_id
            )
            AND (p_last_synced_at IS NULL OR s.updated_at > p_last_synced_at)
        ),

        -- ----------------------------------------------------
        -- ENTITY 15: Remarks (टिप्पणियाँ और उनके लक्षित यूज़र्स)
        -- ----------------------------------------------------
        'remarks', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id', r.id,
                    'parent_organization_id', r.parent_organization_id,
                    'organization_id', r.organization_id,
                    'active_session_id', r.active_session_id,
                    'content', r.content,
                    'category', r.category,
                    'priority', r.priority,
                    'creator_user_id', COALESCE(
                        r.creator_workspace_role_id,
                        (SELECT st.id FROM public.organization_parent_staff st JOIN public.organization_parent_staff_user_links l ON l.staff_id = st.id WHERE l.user_id = r.creator_user_id AND l.parent_organization_id = r.parent_organization_id LIMIT 1),
                        (SELECT s.id FROM public.organization_students s JOIN public.organization_student_user_links l ON l.student_id = s.id WHERE l.user_id = r.creator_user_id LIMIT 1),
                        (SELECT g.id FROM public.organization_guardians g JOIN public.organization_guardian_user_links l ON l.guardian_id = g.id WHERE l.user_id = r.creator_user_id LIMIT 1),
                        r.creator_user_id
                    ),
                    'creator_workspace_role_id', COALESCE(
                        (SELECT sr.name FROM public.organization_parent_staff st JOIN public.global_staff_roles sr ON st.role_id = sr.id WHERE st.id = r.creator_workspace_role_id LIMIT 1),
                        (SELECT 'Student' FROM public.organization_students s WHERE s.id = r.creator_workspace_role_id LIMIT 1),
                        (SELECT 'Guardian' FROM public.organization_guardians g WHERE g.id = r.creator_workspace_role_id LIMIT 1),
                        (CASE 
                            WHEN EXISTS (SELECT 1 FROM public.organization_student_user_links l WHERE l.user_id = r.creator_user_id) THEN 'Student'
                            WHEN EXISTS (SELECT 1 FROM public.organization_guardian_user_links l WHERE l.user_id = r.creator_user_id) THEN 'Guardian'
                            ELSE 'Staff'
                         END)
                    ),
                    'visibility_type', r.visibility_type,
                    'visibility_audience', (
                        CASE 
                            WHEN jsonb_typeof(r.visibility_audience) = 'string' THEN (r.visibility_audience#>>'{}')::jsonb 
                            ELSE r.visibility_audience 
                        END
                    ),
                    'is_pinned', r.is_pinned,
                    'pin_expires_at', r.pin_expires_at,
                    'expires_at', r.expires_at,
                    'target_id', COALESCE(t.id::text, ''),
                    'target_type', COALESCE(t.target_type, ''),
                    'target_student_id', t.target_student_id,
                    'target_guardian_id', t.target_guardian_id,
                    'target_staff_id', t.target_staff_id,
                    'target_user_id', t.target_user_id,
                    'is_active', r.is_active,
                    'is_deleted', r.is_deleted,
                    'created_by', COALESCE(
                        (SELECT st.name FROM public.organization_parent_staff st WHERE st.id = r.creator_workspace_role_id LIMIT 1),
                        (SELECT s.name FROM public.organization_students s WHERE s.id = r.creator_workspace_role_id LIMIT 1),
                        (SELECT g.name FROM public.organization_guardians g WHERE g.id = r.creator_workspace_role_id LIMIT 1),
                        (SELECT st.name FROM public.organization_parent_staff st JOIN public.organization_parent_staff_user_links l ON l.staff_id = st.id WHERE l.user_id = r.creator_user_id AND l.parent_organization_id = r.parent_organization_id LIMIT 1),
                        (SELECT s.name FROM public.organization_students s JOIN public.organization_student_user_links l ON l.student_id = s.id WHERE l.user_id = r.creator_user_id LIMIT 1),
                        (SELECT g.name FROM public.organization_guardians g JOIN public.organization_guardian_user_links l ON l.guardian_id = g.id WHERE l.user_id = r.creator_user_id LIMIT 1),
                        'Unknown'
                    ),
                    'updated_by', r.updated_by::text
                )
            ), '[]'::jsonb)
            FROM public.organization_remarks r
            LEFT JOIN public.organization_remark_targets t ON r.id = t.remark_id
            WHERE r.parent_organization_id = v_resolved_parent_org_id
            AND (p_last_synced_at IS NULL OR r.updated_at > p_last_synced_at OR t.updated_at > p_last_synced_at)
            -- सुरक्षा और विजिबिलिटी फ़िल्टर नियम
            AND (
                -- नियम १: एडमिन/प्रिंसिपल सभी रिमार्क्स देख सकते हैं
                p_user_role IN ('System Administrator', 'School Administrator', 'Org Admin', 'Principal')
                OR
                -- नियम २: रिमार्क बनाने वाला स्वयं उसे देख सकता है
                r.creator_user_id = p_user_id
                OR
                -- नियम ३: छात्र के लिए फ़िल्टर
                (
                    p_user_role = 'Student'
                    AND (t.target_student_id = v_student_id OR t.target_user_id = p_user_id)
                    AND (
                        COALESCE((CASE WHEN jsonb_typeof(r.visibility_audience) = 'string' THEN (r.visibility_audience#>>'{}')::jsonb ELSE r.visibility_audience END), '[]'::jsonb) = '[]'::jsonb
                        OR
                        COALESCE((CASE WHEN jsonb_typeof(r.visibility_audience) = 'string' THEN (r.visibility_audience#>>'{}')::jsonb ELSE r.visibility_audience END), '[]'::jsonb) @> '["Student"]'::jsonb
                    )
                )
                OR
                -- नियम ४: अभिभावक के लिए फ़िल्टर
                (
                    p_user_role = 'Guardian'
                    AND (
                        t.target_guardian_id = v_guardian_id 
                        OR 
                        t.target_student_id IN (SELECT id FROM public.organization_students WHERE guardian_id = v_guardian_id)
                    )
                    AND (
                        COALESCE((CASE WHEN jsonb_typeof(r.visibility_audience) = 'string' THEN (r.visibility_audience#>>'{}')::jsonb ELSE r.visibility_audience END), '[]'::jsonb) = '[]'::jsonb
                        OR
                        COALESCE((CASE WHEN jsonb_typeof(r.visibility_audience) = 'string' THEN (r.visibility_audience#>>'{}')::jsonb ELSE r.visibility_audience END), '[]'::jsonb) @> '["Guardian"]'::jsonb
                    )
                )
                OR
                -- नियम ५: शिक्षक / सामान्य स्टाफ के लिए फ़िल्टर
                (
                    p_user_role NOT IN ('Student', 'Guardian', 'System Administrator', 'School Administrator', 'Org Admin', 'Principal')
                    AND (t.target_staff_id = v_staff_id OR t.target_user_id = p_user_id)
                    AND (
                        COALESCE((CASE WHEN jsonb_typeof(r.visibility_audience) = 'string' THEN (r.visibility_audience#>>'{}')::jsonb ELSE r.visibility_audience END), '[]'::jsonb) = '[]'::jsonb
                        OR
                        COALESCE((CASE WHEN jsonb_typeof(r.visibility_audience) = 'string' THEN (r.visibility_audience#>>'{}')::jsonb ELSE r.visibility_audience END), '[]'::jsonb) @> jsonb_build_array(p_user_role)
                        OR
                        COALESCE((CASE WHEN jsonb_typeof(r.visibility_audience) = 'string' THEN (r.visibility_audience#>>'{}')::jsonb ELSE r.visibility_audience END), '[]'::jsonb) @> '["Staff"]'::jsonb
                        OR
                        COALESCE((CASE WHEN jsonb_typeof(r.visibility_audience) = 'string' THEN (r.visibility_audience#>>'{}')::jsonb ELSE r.visibility_audience END), '[]'::jsonb) @> '["Teacher"]'::jsonb
                    )
                )
            )
        )
    ) INTO v_response;

    RETURN v_response;
END;
$$;
