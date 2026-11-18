package org.springcrazy.modules.book.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.entity.BookCollection;

import java.util.List;

/**
 * <p>
 * 作业
 * </p>
 *
 * @author Ryan
 */
@Data
@ApiModel(value = "Homework对象", description = "作业")
public class BookCollectionVO extends BookCollection {


    private Book book;

}
