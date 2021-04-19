CREATE FUNCTION SP_AUTO_CREATE_DATA_M_BP_PRICE()
 returns int
 language plpgsql
 AS
 $$
/* desc : melakukan insert product ke tabel m_pb_price
          berdasarkan product per kategori yang dipilih
*/

DECLARE
    r RECORD;
    s RECORD;
    t RECORD;
BEGIN
    FOR r IN (select M_PRODUCT_CATEGORY_ID from m_bp_productcatagory)
    LOOP
        FOR s IN (select m_product_id
                  from m_product mp
                  where m_product_category_id = r.M_PRODUCT_CATEGORY_ID --1000020
                  and isactive='Y'
                  order by m_product_id )
        LOOP
            insert into m_bp_price (m_bp_price_id, 	--1
            ad_client_id,							--2
            ad_org_id,								--3
            breakvalue,								--4
            c_bpartner_id,							--5
            created,								--6
            createdby,								--7
            isactive,								--8
            m_product_id ,							--9
            pricelimit ,							--10
            pricelist ,								--11
            pricestd ,								--12
            updated ,								--13
            updatedby ,								--14
            c_currency_id )							--15
            values(
            nextid(53436, 'N'),
            1000000,								--JS Procurement Group
            1000000,								--JS Procuremnt Org
            0,
            1000008,								-- 1000008: JSProcurementGroupUser ATAU 1000009: JSProcurementGroupAdmin
            NOW(),
            1000007,								-- 1000006 : JSProcurementGroupAdmin ATAU 1000007: JSProcurementGroupUser
            'Y',
            s.m_product_id,  --1000008,								-- JS00196_30: SWING TOP TIDY 30LTR
            0,0,0,
            NOW(),1000007,
            120										-- AUD: AUSTRALIAN DOLAR
            );
        END LOOP;
    END LOOP;
    return 1;
END;
$$;

--select bp_category


--selact all product under category


--insert m_bp_price
