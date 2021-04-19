/*******************************************************************************
 * @author Copyright (C) 2019 ICreated, Sergey Polyarus
 *  @date 2019
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms version 2 of the GNU General Public License as published
 *  by the Free Software Foundation. This program is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 ******************************************************************************/
package org.icreated.wstore.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.icreated.wstore.bean.PriceListProduct;
import org.icreated.wstore.bean.ProductCategory;

public class CatalogService extends AService {
	
	
	CLogger log = CLogger.getCLogger(CatalogService.class);
	
	Properties ctx;
	
	
	public CatalogService(Properties ctx) {
		
		this.ctx = ctx;
	}
	
	
	public List<ProductCategory> getCategories() {
		
		List<ProductCategory> retValue = new ArrayList<ProductCategory>();
		 
			String sql = "SELECT t.M_Product_Category_ID, t.Name, t.Description " +
			"FROM M_Product_Category t " +
			"WHERE t.AD_Org_ID= ?  AND t.IsActive='Y' AND t.IsSelfService='Y' " +
			"ORDER BY t.Name";

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, null);
				pstmt.setInt (1, Env.getAD_Org_ID(ctx));
				
				rs = pstmt.executeQuery ();

				while (rs.next ()) {
					retValue.add(new ProductCategory(rs.getInt(1), rs.getString(2), rs.getString(3)));
				}
					

			}
			catch (SQLException ex) {
				log.log(Level.SEVERE, "getCategories - " + sql + " - "+ ex);
			}
			
			finally {
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
			
			return retValue;
	}	
	
	//add by natha 12-april-2021	
	public List<ProductCategory> getZcategories() {
		
		List<ProductCategory> retValue = new ArrayList<ProductCategory>();
		 
			String sql = "SELECT t.M_Product_Category_ID, t.Name, t.Description " +
			"FROM M_Product_Category t " +
			"WHERE t.AD_Org_ID= ?  AND t.IsActive='Y' AND t.IsSelfService='Y' " +
			"ORDER BY t.Name";

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, null);
				pstmt.setInt (1, Env.getAD_Org_ID(ctx));
				
				rs = pstmt.executeQuery ();

				while (rs.next ()) {
					retValue.add(new ProductCategory(rs.getInt(1), rs.getString(2), rs.getString(3)));
				}
					

			}
			catch (SQLException ex) {
				log.log(Level.SEVERE, "getCategories - " + sql + " - "+ ex);
			}
			
			finally {
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
			
			return retValue;
	}
	
	public PriceListProduct getProduct(int M_Product_ID) {
		
		PriceListProduct product = null;
		
/*		REMARK BY NATHA	
		String sql = "SELECT p.M_Product_ID, p.Value, p.Name, p.Description, p.Help, p.DocumentNote, p.ImageURL, pp.PriceStd " +
				"FROM M_Product p " +
				"LEFT JOIN M_ProductPrice pp ON (p.M_Product_ID = pp.M_Product_ID AND pp.M_PriceList_Version_ID = ?) " +
				"WHERE p.isActive='Y' AND p.M_Product_ID = ? AND p.Discontinued='N'";
*/
		
		int mPriceList_Version_ID = CommonService.getM_PriceList_Version_ID(Env.getContextAsInt(ctx, "#M_PriceList_ID"),
				new Timestamp(System.currentTimeMillis()));
		log.log(Level.INFO, "TAMPILKAN INFO PRODUCT - " + M_Product_ID + " - pp.M_PriceList_Version_ID - "+ mPriceList_Version_ID);
		
		//Query Baru ADD BY NATHA
		String sql = "SELECT p.M_Product_ID, p.Value, p.Name, p.Description, p.Help, p.DocumentNote, p.ImageURL, pp.PriceStd " +
		"FROM M_Product p " +
		"LEFT JOIN M_ProductPrice pp ON (p.M_Product_ID = pp.M_Product_ID AND pp.M_PriceList_Version_ID = ?) "+
		"LEFT JOIN C_BPartner_Product bp on (p.m_product_id = bp.m_product_id ) "+
		"LEFT JOIN c_bpartner cb on (cb.c_bpartner_id = bp.c_bpartner_id ) "+
		"WHERE p.isActive='Y' " +
		"AND p.Discontinued='N' " +
		"AND cb.iscustomer ='Y' " +
		"AND p.M_Product_ID = ?";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt (1, CommonService.getM_PriceList_Version_ID(Env.getContextAsInt(ctx, "#M_PriceList_ID"), new Timestamp(System.currentTimeMillis())));		
			pstmt.setInt(2, M_Product_ID);	

			rs = pstmt.executeQuery ();
			
			if (rs.next ()) {
				product = new PriceListProduct(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4),rs.getString(5), rs.getString(6),rs.getString(7),
						rs.getBigDecimal(8));
			}

		}
		catch (SQLException ex)
		{
			log.log(Level.SEVERE,"getProduct - " + sql + " - "+ ex);
		} finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
       }

		return product;		
		
	}
	
	
	
	public  List<PriceListProduct> getProducts(int M_Product_Category_ID, boolean isWebStoreFeatured) {
		
		 List<PriceListProduct> list = new  ArrayList<PriceListProduct>();
			
			String sql = "SELECT p.M_Product_ID, p.Value, p.Name, p.Description, p.Help, p.DocumentNote, p.ImageURL, pp.PriceStd " +
					"FROM M_Product p " +
					"LEFT JOIN M_ProductPrice pp ON (p.M_Product_ID = pp.M_Product_ID AND pp.M_PriceList_Version_ID = ?)  " +
					"WHERE p.IsBOM = 'N' AND p.isActive='Y' AND p.Discontinued='N' ";

			if (M_Product_Category_ID > 0)
				sql +=" AND p.M_Product_Category_ID = ?";
			if (isWebStoreFeatured)
				sql +=" AND p.isWebStoreFeatured = 'Y'";
			sql += " ORDER BY p.Name";
		
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt (1, CommonService.getM_PriceList_Version_ID(Env.getContextAsInt(ctx, "#M_PriceList_ID"), new Timestamp(System.currentTimeMillis())));		

			if (M_Product_Category_ID > 0)
				pstmt.setInt (2, M_Product_Category_ID);

			rs = pstmt.executeQuery ();

			while (rs.next ()) {

				list.add(new PriceListProduct(rs.getInt(1),rs.getString(2),rs.getString(3), rs.getString(4),rs.getString(5),rs.getString(6),
						rs.getString(7),rs.getBigDecimal(8)));
			}
		}
		catch (SQLException ex)
		{
			log.log(Level.SEVERE, "getProducts - " + sql + " - "+ ex);
		} finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
        }
		
		
		return list;		
	}
	
	
	public  List<PriceListProduct> doSearch(String searchString) {
		
		
		 List<PriceListProduct> list = new  ArrayList<PriceListProduct>();
			
			String sql = "SELECT p.M_Product_ID, p.Value, p.Name, p.Description, p.Help, p.DocumentNote, p.ImageURL, pp.PriceStd " +
					"FROM M_Product p " +
					"LEFT JOIN M_ProductPrice pp ON (p.M_Product_ID = pp.M_Product_ID AND pp.M_PriceList_Version_ID = ?)  " +
					"WHERE p.IsBOM = 'N' AND p.isActive='Y' AND p.Discontinued='N' AND UPPER(p.Name || p.Description) LIKE UPPER(?) " +
					"ORDER BY p.Name";
		
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt (1, CommonService.getM_PriceList_Version_ID(Env.getContextAsInt(ctx, "#M_PriceList_ID"), new Timestamp(System.currentTimeMillis())));		
			pstmt.setString (2, searchString);		

			rs = pstmt.executeQuery ();

			while (rs.next ()) {

				list.add(new PriceListProduct(rs.getInt(1),rs.getString(2),rs.getString(3), rs.getString(4),rs.getString(5),rs.getString(6),
						rs.getString(7),rs.getBigDecimal(8)));
			}
		}
		catch (SQLException ex)
		{
			log.log(Level.SEVERE, "getProducts - " + sql + " - "+ ex);
		} finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
       }
		
		
		return list;		
	}
	
	
	public  List<PriceListProduct> getProductsById(List<Integer> ids) {
		
		List<PriceListProduct> retValue = new ArrayList<PriceListProduct>();
		
		String sql = "SELECT p.M_Product_ID, p.Value, p.Name, p.Description, p.Help, p.DocumentNote, p.ImageURL, pp.PriceStd " +
			"FROM M_Product p " +
			"LEFT JOIN M_ProductPrice pp ON (p.M_Product_ID = pp.M_Product_ID AND pp.M_PriceList_Version_ID = ?) " +
			"WHERE p.isActive='Y' AND p.Discontinued='N' AND  p.M_Product_ID IN (%s)";
		
		
		StringBuffer sqlBuffer = new StringBuffer();
		for (Integer id : ids) {
			sqlBuffer.append(id).append(",");
		}
		
		sql = String.format(sql, sqlBuffer.substring(0, sqlBuffer.length()-1));
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt (1, CommonService.getM_PriceList_Version_ID(Env.getContextAsInt(ctx, "#M_PriceList_ID"), new Timestamp(System.currentTimeMillis())));		

			rs = pstmt.executeQuery ();
			
			while (rs.next ()) {
				
				retValue.add(new PriceListProduct(rs.getInt(1),rs.getString(2),rs.getString(3), rs.getString(4),rs.getString(5),rs.getString(6),
						rs.getString(7),rs.getBigDecimal(8)));
			}

				
		}
		catch (SQLException ex)
		{
			log.log(Level.SEVERE, "getList- " + sql + " - "+ ex);
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return retValue;

	}

	//author: natha @Sabtu:03-april-2021
	public List<ProductCategory> getUserProdCategories(String userId) {
		List<ProductCategory> retValue = new ArrayList<ProductCategory>();

		//SELECT Business Partner ID from AD_User
//		String sql2 = "SELECT C_BPARTNER_ID " +
//				"FROM AD_USER " +
//				"WHERE EMAIL = ?";

		String sql = "SELECT t.M_Product_Category_ID, t.Name, t.Description " +
				"FROM M_Product_Category t " +
				"INNER JOIN M_BP_PRODUCTCATAGORY m ON (t.M_Product_Category_ID = m.M_Product_Category_ID)" +
				"WHERE t.AD_Org_ID= ?  AND t.IsActive='Y' AND t.IsSelfService='Y' " +
				"AND M.C_BPARTNER_ID = (SELECT C_BPARTNER_ID FROM AD_USER WHERE EMAIL = ?) " +
				"ORDER BY t.Name";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt (1, Env.getAD_Org_ID(ctx));
//			pstmt.emailID (2, userId);
			pstmt.setString(2, userId);

			rs = pstmt.executeQuery ();

			while (rs.next ()) {
				retValue.add(new ProductCategory(rs.getInt(1), rs.getString(2), rs.getString(3)));
			}


		}
		catch (SQLException ex) {
			log.log(Level.SEVERE, "getProductCategories by emailID - " + sql + " - "+ ex);
		}

		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return retValue;
	}

}
